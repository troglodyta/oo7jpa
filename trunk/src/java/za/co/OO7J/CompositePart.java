package za.co.OO7J;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import za.co.OO7J.utils.GeneralParameters;
import za.co.OO7J.utils.LinkingMap;
import za.co.OO7J.utils.NewPersistence;
import za.co.OO7J.utils.RandomUtil;
import za.co.OO7J.utils.SettingsUtil;

/**
 * @author pvz 19-Apr-2006
 * 
 * @hibernate.class table="composite_parts"
 * 
 */
@Entity
@Table(name = "composite_parts")
// @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NamedQueries({
    @NamedQuery(name = "compositePart.selectCompositePartWithId", query = "select comp from CompositePart comp where comp.designId = :id")
})
public class CompositePart extends DesignObject implements Serializable {

    // pvz: hibernate specific
    private long composite_part_id;
    private Document documentation = null;
    // relationship: componentsPriv
    private Set usedInPrivate = null;// set of BaseAssemblies
    // relationship: componentsShared
    private Set usedInShared = null;// set of BaseAssemblies
    // relationship: partOF
    private List parts = null;// Set of AtomicParts
    private AtomicPart rootPart = null;
    // NEW PVZ
    private static int total_parts = 0;// DEBUG

    /**
     *
     * 21-Apr-2006
     *
     * Needed by Hibernate
     */
    public CompositePart() {
    }

    public CompositePart(int compositePartID) {

        //
        SettingsUtil settingsUtil = SettingsUtil.getInstance();
        if (SettingsUtil.debugMode) {
            System.out.println("CompositePart()-> cpId = " + compositePartID);
        }
        this.setDesignId(compositePartID);

        // initialize the simple stuff

        // TODO: PVZ: SET THE TYPE
        setType("CompositePart");

        // TODO: PVZ: SET THE BUILD DATE: EITHER YOUNG OR OLD
        // for the build date, decide if this part is young or old, and then
        // randomly choose a date in the required range

        if ((getDesignId() % GeneralParameters.youngCompFrac) == 0) {
            // young one
            if (SettingsUtil.debugMode) {
                System.out.println("young composite part, id = "
                        + getDesignId());
            }

            setBuildDate(GeneralParameters.minYoungCompDate
                    + (RandomUtil.nextPositiveInt() % (GeneralParameters.maxYoungCompDate
                    - GeneralParameters.minYoungCompDate + 1)));

        } else {
            // old one
            if (SettingsUtil.debugMode) {
                System.out.println("old composite part, id = " + getDesignId());
            }

            setBuildDate(GeneralParameters.minOldCompDate
                    + (RandomUtil.nextPositiveInt() % (GeneralParameters.maxOldCompDate
                    - GeneralParameters.minOldCompDate + 1)));

        }

        Document document = new Document(compositePartID, this);
        NewPersistence.save(document);
        setDocumentation(document);

        // now create the atomic parts (indexed by their ids) ...

        int numAtomicPerComp = SettingsUtil.NumAtomicPerComp;
        int atomicId = 0;

        NewPersistence.saveOrUpdate(this);
        AtomicPart atomicPart = null;
        for (int i = 0; i < numAtomicPerComp; i++) {

            atomicId = SettingsUtil.nextAtomicId + i;
            // atomicId++; or ++atomicID if we start from 0 and not 1

            /*
             * create a new atomic part the AtomicPart constructor takes care of
             * setting up the back pointer to the containing CompositePart()
             */
            if (getParts() == null) {
                setParts(new ArrayList());
            }
            atomicPart = new AtomicPart(atomicId, this);
            NewPersistence.save(atomicPart);
            getParts().add(atomicPart);
        }// end for

        if (SettingsUtil.debugMode) {// pvz: new debug
            total_parts += getParts().size();
            System.out.println("CompositePart() total parts size>:  "
                    + total_parts);
        }

        // first atomic part is the root part
        setRootPart((AtomicPart) parts.get(0));

        /*
         * ... and then wire them semi-randomly together (as a ring plus random
         * additional connections to ensure full part reachability for
         * traversals)
         */

        int numConnPerAtomic = SettingsUtil.NumConnPerAtomic;

        AtomicPart fromAtomicPart = null;
        AtomicPart toAtomicPart = null;
        za.co.OO7J.Connection connection = null;

        for (int from = 0; from < numAtomicPerComp; from++) {

            fromAtomicPart = (AtomicPart) getParts().get(from);

            // OZONE's implementation calls this the next
            int to = (from + 1) % numAtomicPerComp;

            // OZONE's implementation has i< numConnPerAtomic-1???? Why?
            for (int i = 0; i < numConnPerAtomic; i++) {
                toAtomicPart = (AtomicPart) getParts().get(to);

                connection = new za.co.OO7J.Connection(fromAtomicPart,
                        toAtomicPart);
                // //Note: pvz: i can remove this then move it to the the last
                // line in the Connection constructor
                NewPersistence.saveOrUpdate(connection);
                NewPersistence.saveOrUpdate(toAtomicPart);

                to = RandomUtil.nextPositiveInt() % numAtomicPerComp;
            }// end for i
            NewPersistence.saveOrUpdate(fromAtomicPart);

        }// end for from

        // TODO: NOTE This part seems to be missing from OZONE's OO7
        // implementation!!!!!!!

        // Increment the id for next set of AtomicParts for next Composite Part
        // TODO: NOTE pvz: why is this needed?
        SettingsUtil.nextAtomicId += numAtomicPerComp;

        /*
         * finally insert this composite part as a child of the base assemblies
         * that use it first the assemblies using the comp part as a
         * usedInShared component get the first base assembly baseAssembly =
         * Shared_cp[cpId].next();
         */

        // first the assemblies using the comp part as a usedInShared component
        if (SettingsUtil.debugMode) {
            System.out.println("Starting a scan on Shared_cp: "
                    + compositePartID);
        }

        BaseAssembly baseAssemblyShared = null;

        LinkingMap linkingMap = settingsUtil.getLinkingMap();
        BAidList baIdList = null;

        if ((linkingMap.getSharedCompositePartIDs() != null)
                && linkingMap.getSharedCompositePartIDs().size() > 0) {

            // System.out.println("shared part ids size: "
            // + linkingMap.getSharedCompositePartIDs().size());
            // System.out.println("shared part ids size: "
            // + linkingMap.getSharedCompositePartIDs().containsKey(
            // new Long(compositePartID)));

            baIdList = linkingMap.getSharedCompositePartIDs().get(
                    new Long(compositePartID));
            if (baIdList != null) {
                // System.out.println("shared BA ID LIST NOT EMPTY");

                Set<Long> listOfBAids = baIdList.getBaIdList();

                for (Iterator iter = listOfBAids.iterator(); iter.hasNext();) {
                    Long baseAssemblyID = (Long) iter.next();
                    baseAssemblyShared = NewPersistence.findOneBaseAssembly(baseAssemblyID.intValue());

                    if (baseAssemblyShared != null) {

                        addShared(baseAssemblyShared);
                        NewPersistence.saveOrUpdate(baseAssemblyShared);

                    } else {
                        // System.out
                        // .println("pvz: Why is the base assembly null for
                        // shared?");
                    }// end if-else equals

                }// END FOR
            }
        }

        /*
         *
         * Next the assemblies using the comp part as a usedInPrivate component.
         * Get the first base assembly
         *
         */

        BaseAssembly baseAssemblyPrivate = null;

        linkingMap = settingsUtil.getLinkingMap();

        if ((linkingMap.getPrivateCompositePartIDs() != null)
                && linkingMap.getPrivateCompositePartIDs().size() > 0) {

            // System.out.println("private part ids size: "
            // + linkingMap.getPrivateCompositePartIDs().size());
            // System.out.println("private part ids size: "
            // + linkingMap.getSharedCompositePartIDs().containsKey(
            // new Long(compositePartID)));

            baIdList = linkingMap.getPrivateCompositePartIDs().get(
                    new Long(compositePartID));

            if (baIdList != null) {
                // System.out.println("private BA ID LIST NOT EMPTY");
                Set<Long> listOfBAids = baIdList.getBaIdList();

                for (Iterator iter = listOfBAids.iterator(); iter.hasNext();) {
                    Long baseAssemblyID = (Long) iter.next();
                    baseAssemblyPrivate = NewPersistence.findOneBaseAssembly(baseAssemblyID.intValue());

                    if (baseAssemblyPrivate != null) {

                        addPrivate(baseAssemblyPrivate);
                        NewPersistence.saveOrUpdate(baseAssemblyPrivate);
                    } else {
                        // System.out
                        // .println("pvz: Why is the base assembly null for
                        // private?");
                    }

                }// END FOR
            }
        }

    }

    /**
     * @return 19-Apr-2006 name="documentation"
     *
     * @hibernate.many-to-one column="document_id" unique="true" not-null="true"
     */
    @OneToOne
    //@ForeignKey(name = "document_id")
    public Document getDocumentation() {
        return documentation;
    }

    public void setDocumentation(Document documentation) {
        this.documentation = documentation;
    }

    /**
     * @return 20-Apr-2006
     *
     * TODO: tip: ad inverse to the many side name="parts"
     *
     * @hibernate.list
     *
     * @hibernate.key column="composite_part_id" not-null="true"
     *
     * @hibernate.list-index column="parts_id"
     *
     * @hibernate.one-to-many class="za.co.OO7J.AtomicPart"
     */
    @OneToMany(mappedBy = "partOf", cascade = javax.persistence.CascadeType.ALL, targetEntity = AtomicPart.class)
    //PJWD: @JoinColumn(name = "composite_part_id")
    public List getParts() {
        return parts;
    }

    public void setParts(List parts) {
        this.parts = parts;
    }

    /**
     * @return 20-Apr-2006
     *
     * name="rootPart"
     *
     * @hibernate.many-to-one column="atomic_part_id" unique="true"
     *                        not-null="false"
     */
    //PJWD: was ManyToOne
    @OneToOne(cascade = javax.persistence.CascadeType.ALL)
    //@ForeignKey(name = "atomic_part_id")
    @JoinColumn(name = "atomic_part_id")
    public AtomicPart getRootPart() {
        return rootPart;
    }

    public void setRootPart(AtomicPart rootPart) {
        this.rootPart = rootPart;
    }

    /**
     * @return 19-Apr-2006 name="usedInPriv"
     *
     * @hibernate.set inverse="true" table="ba_cp_private"
     *
     * @hibernate.key column="composite_part_id"
     *
     * @hibernate.many-to-many class="za.co.OO7J.BaseAssembly"
     *                         column="base_assembly_id"
     */
    @ManyToMany(targetEntity = BaseAssembly.class)
    @JoinTable(name = "ba_cp_private", joinColumns = {
        @JoinColumn(name = "composite_part_id")}, inverseJoinColumns = {
        @JoinColumn(name = "base_assembly_id")})
    //@ForeignKey(name = "composite_part_id", inverseName = "base_assembly_id")
    public Set getUsedInPrivate() {
        return usedInPrivate;

    }

    public void setUsedInPrivate(Set usedInPrivate) {
        this.usedInPrivate = usedInPrivate;
    }

    /**
     * @return 19-Apr-2006
     *
     * name="usedInShar"
     *
     * @hibernate.set inverse="true" table="ba_cp_shared"
     *
     * @hibernate.key column="composite_part_id"
     *
     * @hibernate.many-to-many class="za.co.OO7J.BaseAssembly"
     *                         column="base_assembly_id"
     */
    @ManyToMany(targetEntity = BaseAssembly.class)
    @JoinTable(name = "ba_cp_shared", joinColumns = {
        @JoinColumn(name = "composite_part_id")}, inverseJoinColumns = {
        @JoinColumn(name = "base_assembly_id")})
    //@ForeignKey(name = "composite_part_id", inverseName = "base_assembly_id")
    public Set getUsedInShared() {
        return usedInShared;
    }

    public void setUsedInShared(Set usedInShared) {
        this.usedInShared = usedInShared;
    }

    /*
     *
     * HIBERNATE SPECIFIC
     *
     */
    /**
     * @return 19-Apr-2006
     *
     * @hibernate.id generator-class="native" column="composite_part_id"
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public long getComposite_part_id() {
        return composite_part_id;
    }

    public void setComposite_part_id(long composite_part_id) {
        this.composite_part_id = composite_part_id;
    }
    private int designId = 0;
    private String type = "";
    private long buildDate = 0;

    /**
     *
     * @hibernate.property name="buildDate" column="build_Date"
     *
     */
    @Override
    public long getBuildDate() {
        return buildDate;
    }

    @Override
    public void setBuildDate(long buildDate) {
        this.buildDate = buildDate;
    }

    /**
     * @hibernate.property name="designId" column="design_id"
     *
     */
    //@Index(name = "composite_part_id_index")
    @Override
    public int getDesignId() {
        return designId;
    }

    /**
     * 01-May-2006
     *
     * @param designId
     *            the designId to set
     */
    @Override
    public void setDesignId(int designId) {
        this.designId = designId;
    }

    /**
     * @hibernate.property name="type" column="type"
     */
    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    private void addPrivate(BaseAssembly baseAssemblyPrivate) {

        if (SettingsUtil.debugMode) {
            System.out.println("Adding BaseAssembly private: "
                    + baseAssemblyPrivate.getDesignId());
        }// end if debugMode

        if (SettingsUtil.debugMode) {
            System.out.println("CompositePart()->Priv::Adding this:"
                    + this.getDesignId() + " to baseAssembly: "
                    + baseAssemblyPrivate.getDesignId());
        }
        /*
         * first add this assembly to the list of assemblies in which this
         * composite part is used as a private member
         */
        if (getUsedInPrivate() == null) {
            setUsedInPrivate(new HashSet());
        }

        getUsedInPrivate().add(baseAssemblyPrivate);

        /*
         * add this composite part to the list of usedInPrivate parts usedin the
         * assembly
         */
        if (baseAssemblyPrivate.getComponentsPrivate() == null) {
            baseAssemblyPrivate.setComponentsPrivate(new HashSet());
        }

        baseAssemblyPrivate.getComponentsPrivate().add(this);

        // System.out.println("BaseAssembly size of private: "
        // + baseAssemblyPrivate.getComponentsPrivate().size());

    }

    private void addShared(BaseAssembly baseAssemblyShared) {

        /*
         * add this assembly to the list of assemblies in which this composite
         * part is used as a shared member
         */
        if (getUsedInShared() == null) {
            setUsedInShared(new HashSet());
        }
        getUsedInShared().add(baseAssemblyShared);

        /*
         * add this composite part cp the list of usedInShared parts used in the
         * assembly
         */

        if (baseAssemblyShared.getComponentsShared() == null) {
            baseAssemblyShared.setComponentsShared(new HashSet());
        }
        baseAssemblyShared.getComponentsShared().add(this);

        if (SettingsUtil.debugMode) {
            System.out.println("Adding BaseAssembly shared: "
                    + baseAssemblyShared.getDesignId());
        }// end if

    }
}
