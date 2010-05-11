package za.co.OO7J;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.OneToMany;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import za.co.OO7J.utils.GeneralParameters;
import za.co.OO7J.utils.RandomUtil;
import za.co.OO7J.utils.SettingsUtil;

//PJWD: edited the connections between connection and
//atomicpart, this isn't many-to-many but two times one-to-many

/**
 * @author pvz 20-Apr-2006
 * 
 * @hibernate.class table="atomic_parts" lazy="false"
 * 
 */
@Entity
@Table(name = "atomic_parts")
@NamedQueries({
    @NamedQuery(name = "module.selectOneAtomicPart", query = "select atomic from AtomicPart atomic where atomic.designId = :id"),
    @NamedQuery(name = "module.selectAllAtomicParts", query = "select atomic from AtomicPart atomic"),
    @NamedQuery(name = "selectAllAtomicPartsAndAssociatedDocs", query = "select document, part from Document document, AtomicPart part where document.docId = part.docId"),
    @NamedQuery(name = "selectAtomicPartsInRange", query = "select atomic from AtomicPart atomic where atomic.buildDate >= :min and atomic.buildDate <= :max")
})
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AtomicPart extends DesignObject implements Serializable {

    private int x, y;
    private int docId;
    private Set toConnections = null;// set of Connections
    private Set fromConnections = null;// set of Connections
    // relationship: parts
    private CompositePart partOf = null;
    //private List rootPartOf = null;

    /**
     *
     * 21-Apr-2006
     *
     * Needed by Hibernate
     */
    public AtomicPart() {
    }

    public AtomicPart(int newId, CompositePart compositePart) {

        if (SettingsUtil.debugMode) {
            System.out.println("AtomicPart()-> ptId = " + newId);
        }

        setDesignId(newId);
        setPartOf(compositePart);

        // TODO: PVZ: SET THE TYPE
        setType("AtomicPart");

        // TODO: PVZ: SET THE BUILD DATE: EITHER YOUNG OR OLD
        setBuildDate(GeneralParameters.minAtomicDate
                + (RandomUtil.nextPositiveInt() % (GeneralParameters.maxAtomicDate
                - GeneralParameters.minAtomicDate + 1)));

        setX(RandomUtil.nextPositiveInt() % GeneralParameters.xyRange);
        setY(RandomUtil.nextPositiveInt() % GeneralParameters.xyRange);

        // fill in a random document id (for query 9)

        int tmpDocId = (RandomUtil.nextInt() % SettingsUtil.TotalCompParts) + 1;
        setDocId(tmpDocId);

    }

    /**
     *
     * 06-May-2006
     */
    public void swapXY() {

        if (SettingsUtil.debugMode) {
            System.out.println("AtomicPart.swapXY()-> ptId = "
                    + this.getDesignId() + " x = " + getX() + " y = " + getY());
        }

        // dirty();

        // exchange X and Y values
        //PJWD: don't use direct property access
        int tmp = getX();
        setX(getY());
        setY(tmp);

        if (SettingsUtil.debugMode) {
            System.out.println("[did swap, so x = " + getX() + " and y ="
                    + getY() + "]");
        }

    }

    /**
     *
     * 06-May-2006
     */
    public void toggleDate() {

        if (SettingsUtil.debugMode) {
            System.out.println("AtomicPart.toggleDate()-> ptId = "
                    + this.getDesignId() + " buildDate = " + getBuildDate());
        }

        // put the change into the DB

        // increment build date if odd, decrement it if even

        // dirty();

        if (getBuildDate() % 2 == 0) {
            // even case
            setBuildDate(getBuildDate() - 1);
        } else {
            // odd case
            setBuildDate(getBuildDate() + 1);
        }

        if (SettingsUtil.debugMode) {
            System.out.println("[did toggle, so buildDate = " + getBuildDate());
        }

    }

    /**
     * @return 21-Apr-2006
     *
     * @hibernate.property column="doc_id"
     */
    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    /**
     * @return 21-Apr-2006
     *
     * for the to and from set I used the same key column: atomic_part_id This
     * caused a problem when retrieving these connection sets and atomic parts.
     * Only 260 where returned instead of 2000. changing these key columns to
     * different individual columns fixed this issue
     *
     * inverse="true" is very important here! When I left it out I got an lazy
     * proxy loading error with a missing method that is added in the byte code!
     * This happend in Query4!!!! EnhancerByCGLIB
     *
     * name="atomic_from_connections"
     *
     * @hibernate.set table="from_connections" lazy="false"
     *
     * @hibernate.key column="atomic_part_id"
     *
     * @hibernate.many-to-many class="za.co.OO7J.Connection"
     *                         column="connection_id" unique="true"
     */
    //PJWD: was many-to-many
    @OneToMany(targetEntity = Connection.class
    , fetch = javax.persistence.FetchType.LAZY
    , cascade = javax.persistence.CascadeType.ALL
    , mappedBy="from")
    //@JoinTable(name = "from_connections", joinColumns = {
    //    @JoinColumn(name = "atomic_part_id")}, inverseJoinColumns = {
    //    @JoinColumn(name = "connection_id")})
        //@ForeignKey(name = "atomic_part_id", inverseName = "connection_id")
    //PJWD @LazyCollection(value=LazyCollectionOption.TRUE)
    //PJWD @Cascade( { org.hibernate.annotations.CascadeType.ALL })
    public Set getFromConnections() {
        return fromConnections;
    }

    public void setFromConnections(Set fromConnections) {
        this.fromConnections = fromConnections;
    }

    /**
     * @return 21-Apr-2006
     *
     * name="rootPart"
     *
     * @hibernate.one-to-one property-ref="rootPart"
     *
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "composite_part_id")
    //PJWD: @ForeignKey(name = "composite_part_id")
    public CompositePart getPartOf() {
        return partOf;
    }

    public void setPartOf(CompositePart partOf) {
        this.partOf = partOf;
    }

    /* Failed try
    @OneToMany(targetEntity = CompositePart.class)
    public List getRootPartOf() {
    return rootPartOf;
    }

    public void setRootPartOf(List partOf) {
    this.rootPartOf = partOf;
    }
     */
    /**
     * @return 21-Apr-2006
     *
     * inverse="true" is very important here! When I left it out I got an lazy
     * proxy loading error with a missing method that is added in the byte code!
     * This happend in Query4!!!! EnhancerByCGLIB
     *
     * name="atomic_to_connections"
     *
     * @hibernate.set table="to_connections" lazy="false"
     *
     * @hibernate.key column="atomic_part_id"
     *
     * @hibernate.many-to-many class="za.co.OO7J.Connection"
     *                         column="connection_id" unique="true"
     *
     */
    //PJWD: was many-to-many
    @OneToMany(targetEntity = Connection.class
    , fetch = javax.persistence.FetchType.LAZY
    , cascade = javax.persistence.CascadeType.ALL
    , mappedBy="to")
    //@JoinTable(name = "to_connections", joinColumns = {
    //    @JoinColumn(name = "atomic_part_id")}, inverseJoinColumns = {
    //    @JoinColumn(name = "connection_id")})
    //PJWD: @ForeignKey(name = "atomic_part_id", inverseName = "connection_id")
    //PJWD: @LazyCollection(value=LazyCollectionOption.TRUE)
    //PJWD: @Cascade( { org.hibernate.annotations.CascadeType.ALL })
    public Set getToConnections() {
        return toConnections;
    }

    public void setToConnections(Set toConnections) {
        this.toConnections = toConnections;
    }

    /**
     * @return 21-Apr-2006
     *
     * @hibernate.property column="x"
     */
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return 21-Apr-2006
     *
     * @hibernate.property column="y"
     */
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static void doNothing() {
    }

    /*
     *
     * HIBERNATE SPECIFIC
     *
     */
    // pvz: hibernate specific
    private long atomic_part_id;

    /**
     * @return 20-Apr-2006
     *
     * @hibernate.id generator-class="native" column="atomic_part_id"
     */
    @Id
    @Column(name = "atomic_part_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public long getAtomic_part_id() {
        return atomic_part_id;
    }

    public void setAtomic_part_id(long atomic_part_id) {
        this.atomic_part_id = atomic_part_id;
    }
    private int designId = 0;
    private String type = "";
    private long buildDate = 0;

    /**
     *
     * @hibernate.property name="buildDate" column="build_Date"
     *                     index="build_date_index"
     *
     */
    //@Index(name = "build_date_index")
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
     *                     index="atomic_part_id_index"
     *
     */
    //@Index(name = "atomic_part_id_index")
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
}
