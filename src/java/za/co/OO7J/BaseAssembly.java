package za.co.OO7J;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import za.co.OO7J.utils.GeneralParameters;
import za.co.OO7J.utils.RandomUtil;
import za.co.OO7J.utils.SettingsUtil;

/**
 * @author pvz TODO:Note: CompositeParts are created first before baseAssemblies
 * 
 * joined subclass in 9.1.2 in Hibernate documentation
 * 
 * @hibernate.joined-subclass name="za.co.OO7J.BaseAssembly"
 *                            table="base_assembly"
 * 
 */
@Entity
@Table(name = "base_assembly")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NamedQueries( {
@NamedQuery(name="module.selectOneBaseAssembly", query="select ba from BaseAssembly ba where ba.designId = :id"),
@NamedQuery(name="module.selectAllBaseAssemblies", query="select ba from BaseAssembly as ba")
})
public class BaseAssembly extends Assembly {

	// pvz: hibernate specific
	private long base_assembly_id;

	private Set componentsPrivate;

	private Set componentsShared;

	/**
	 * 
	 * 21-Apr-2006
	 * 
	 * Needed by Hibernate
	 */
	public BaseAssembly() {

	}

	public BaseAssembly(int newId, Module module, ComplexAssembly parentAssembly) {

		setDesignId(newId);

		// TODO: PVZ: SET THE TYPE
		/*
		 * not sure what these mean: char types[NumTypes] = { "type000",
		 * "type001", "type002", "type003", "type004", "type005", "type006",
		 * "type007", "type008", "type009" };
		 * 
		 * I am just going to store the object class type as type.
		 */
		setType("BaseAssembly");
		// TODO: PVZ: SET THE BUILD DATE:
		setBuildDate(GeneralParameters.minAssmDate
				+ (RandomUtil.nextPositiveInt() % (GeneralParameters.maxAssmDate
						- GeneralParameters.minAssmDate + 1)));

		if (SettingsUtil.debugMode) {
			int numAssmLevels = SettingsUtil.NumAssmLevels;
			System.out.println("BaseAssembly asId = " + newId + " levelNo= "
					+ numAssmLevels);
		}

		if (parentAssembly != null) {
			setSuperAssembly(parentAssembly);
		}
		// all Base Assemblies are added to the module
		if (module.getAssemblies() == null) {
			module.setAssemblies(new HashSet());
		}
	
		module.getAssemblies().add(this);
	

		if (SettingsUtil.debugMode) {

			System.out.println("BaseAssembly module.getAssemblies() SIZE: "
					+ module.getAssemblies().size());
		}

		// pvz this is new to get the global settings:
		SettingsUtil settingsUtil = SettingsUtil.getInstance();

		// get access to the design library containing composite parts
		int numCompPerModule = SettingsUtil.NumCompPerModule;

		long lowCompId = (module.getDesignId() - 1) * numCompPerModule + 1;
		long compIdLimit = numCompPerModule;

		// first select the private composite parts for this assembly
		int numCompPerAssm = SettingsUtil.NumCompPerAssm;

		if (SettingsUtil.debugMode) {
			// pvz my extra comment
			System.out
					.println("BaseAssembly()->Adding composite parts. numCompPerAssm: "
							+ numCompPerAssm);
			System.out
					.println("BaseAssembly()->Adding composite parts. lowCompId: "
							+ lowCompId + " compIdLimit" + compIdLimit);
		}

		for (int i = 0; i < numCompPerAssm; i++) {
			// TODO:NOTE pvz: we could also have used
			// Random().nextInt(compIdLimit) then 0<=next <compIdLimit
			long compId = lowCompId + (RandomUtil.nextInt() % compIdLimit);
			/*
			 * keep track of which CompositePart uses this base assembly as
			 * private Private_cp[compId].insert(self);
			 */

//			if (settingsUtil.getLinkingMap().getPrivateCompositePartIDs()
//					.containsKey((new Long(compId)))) {
//				System.out.println(" This id already exists!!!!!!!!!!!!!!!!!: "
//						+ compId);
//			}
			
			BAidList baIdList = settingsUtil.getLinkingMap()
					.getPrivateCompositePartIDs().get(new Long(compId));
			if (baIdList == null) {
//				System.out.println(" creating ba id list");
				baIdList = new BAidList();
				baIdList.setBaIdList(new TreeSet<Long>());
			}
			baIdList.getBaIdList().add(new Long(this.getDesignId()));
//			System.out.println(" >>> baIdList size: "
//					+ baIdList.getBaIdList().size());
			settingsUtil.getLinkingMap().getPrivateCompositePartIDs().put(
					new Long(compId), baIdList);

			if (SettingsUtil.debugMode) {
				System.out
						.println("BaseAssembly()->Adding private composite parts with compId: "
								+ compId);
			}
		}

		// next select the shared composite parts for this assembly
		if (SettingsUtil.debugMode) {
			// printf("}, Shared parts = { ");
		}

		// TODO: OZONE's implementation only sets the shared components.
		for (int i = 0; i < numCompPerAssm; i++) {
			long compId = (RandomUtil.nextInt() % SettingsUtil.TotalCompParts) + 1;
			/*
			 * uses this base assembly as shared
			 * Shared_cp[compositePartId].insert(self); keep track of which CP
			 * uses this base assembly as shared
			 */

//			if (settingsUtil.getLinkingMap().getSharedCompositePartIDs()
//					.containsKey(new Long(compId))) {
//				System.out.println(" This id already exists!!!!!!!!!!!!!!!!!: "
//						+ compId);
//			}
			
			BAidList baIdList = settingsUtil.getLinkingMap()
					.getSharedCompositePartIDs().get(new Long(compId));
			if (baIdList == null) {
//				System.out.println(" creating ba id list");
				baIdList = new BAidList();
				baIdList.setBaIdList(new TreeSet<Long>());
			}
			baIdList.getBaIdList().add(new Long(this.getDesignId()));

			settingsUtil.getLinkingMap().getSharedCompositePartIDs().put(
					new Long(compId), baIdList);

			if (SettingsUtil.debugMode) {
				System.out
						.println("BaseAssembly()->Adding shared composite parts with compId: "
								+ compId);
			}
		}

	}

	/**
	 * @return 19-Apr-2006
	 * 
	 * name="componentsPriv"
	 * 
	 * @hibernate.set table="components_private" cascade="save-update"
	 * 
	 * @hibernate.key column="base_assembly_id"
	 * 
	 * @hibernate.many-to-many class="za.co.OO7J.CompositePart"
	 *                         column="composite_part_id"
	 */
	@ManyToMany(targetEntity = CompositePart.class
            , fetch = javax.persistence.FetchType.LAZY)
	@JoinTable(name = "components_private", joinColumns = { @JoinColumn(name = "base_assembly_id") }, inverseJoinColumns = { @JoinColumn(name = "composite_part_id") })
	//PJWD: @LazyCollection(value=LazyCollectionOption.TRUE)
	//@#Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	public Set getComponentsPrivate() {
		return componentsPrivate;
	}

	public void setComponentsPrivate(Set componentsPrivate) {
		this.componentsPrivate = componentsPrivate;
	}

	/**
	 * @return 19-Apr-2006 name="componentsShar"
	 * 
	 * @hibernate.set table="components_shared" cascade="save-update"
	 * 
	 * @hibernate.key column="base_assembly_id"
	 * 
	 * @hibernate.many-to-many class="za.co.OO7J.CompositePart"
	 *                         column="composite_part_id"
	 */
	@ManyToMany(targetEntity = CompositePart.class
            , fetch = javax.persistence.FetchType.LAZY)
	@JoinTable(name = "components_shared", joinColumns = { @JoinColumn(name = "base_assembly_id") }, inverseJoinColumns = { @JoinColumn(name = "composite_part_id") })
	//PJWD: @LazyCollection(value=LazyCollectionOption.TRUE)
        //@#Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	public Set getComponentsShared() {
		return componentsShared;
	}

	public void setComponentsShared(Set componentsShared) {
		this.componentsShared = componentsShared;
	}

	public static void doNothing() {

	}

	/*
	 * 
	 * HIBERNATE SPECIFIC
	 * 
	 */

}
