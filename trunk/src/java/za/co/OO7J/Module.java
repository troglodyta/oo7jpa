package za.co.OO7J;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import za.co.OO7J.utils.GeneralParameters;
import za.co.OO7J.utils.NewPersistence;
import za.co.OO7J.utils.RandomUtil;
import za.co.OO7J.utils.SettingsUtil;

/**
 * @author pvz 19-Apr-2006
 * 
 * @hibernate.class table="module"
 * 
 */
@Entity
@Table(name="module")
@NamedQueries( {
@NamedQuery(name="module.selectOneModule", query="select module from Module module where module.designId = :id"),
@NamedQuery(name="module.selectAllModules", query="select module from Module module")
}
)

public class Module extends DesignObject implements Serializable {

	// hibernate specific
	private long module_id;

	private Manual manual = null;

	private Set assemblies = null;

	private ComplexAssembly designRoot = null;

	/**
	 * 
	 * 21-Apr-2006
	 * 
	 * Needed by Hibernate
	 */
	public Module() {

	}

	public Module(int moduleId) {

		if (SettingsUtil.debugMode) {
			System.out.println("Module()-> modId = " + moduleId);
		}

		setDesignId(moduleId);

		// initialize the simple stuff

		// TODO: PVZ: SET THE TYPE
		setType("Module");
		// TODO: PVZ: SET THE BUILD DATE: EITHER YOUNG OR OLD
		setBuildDate(GeneralParameters.minModuleDate
				+ (RandomUtil.nextPositiveInt() % (GeneralParameters.maxModuleDate
						- GeneralParameters.minModuleDate + 1)));

		// Build a manual
		manual = new Manual(getDesignId(), this);
		NewPersistence.saveOrUpdate(manual);

		int numAssmPerAssm = SettingsUtil.NumAssmPerAssm;
		int numAssmLevels = SettingsUtil.NumAssmLevels;

		// Reserve space for assemblies
		int size = numAssmPerAssm;

		for (int n = 1; n < numAssmLevels; n++) {
			size *= numAssmPerAssm;
		}

		// assemblies.capacity(size);

		// now create the assemblies for the module
		if (numAssmLevels > 1) {
			int assmId = SettingsUtil.nextComplexAssemblyId++;

			designRoot = new ComplexAssembly(assmId, this, null, 1);
			NewPersistence.saveOrUpdate(designRoot);
		}
		NewPersistence.saveOrUpdate(this);

	}

	/**
	 * 
	 * Original comment:
	 * 
	 * returns number of occurrences of the character 'I' in the module's
	 * manual.
	 * 
	 * @return 24-Apr-2006
	 */
	public int scanManual() {
		if (getManual() == null) {
			System.out.println("scanManual: manual is null!");
			return -1;
		} else {
			return getManual().searchText("I");
		}

	}

	/**
	 * 
	 * returns 1 if the first and last characters in the manual are the same,
	 * zero otherwise.
	 * 
	 * @return 24-Apr-2006
	 */
	public int firstLast() {

		return getManual().firstLast();
	}

	/**
	 * @return 19-Apr-2006
	 * 
	 * ALSO changed the mapping according to tips from hibernate:ch 7.3.1
	 *  name="assemblies"
	 *  
	 * @hibernate.set table="assemblies"
	 * 
	 * @hibernate.key column="module_id"
	 * 
	 * @hibernate.many-to-many column="assembly_id" unique="true"
	 *                                    class="za.co.OO7J.BaseAssembly"
	 *
	 * 
	 * 
	 * 
	 */
        //PJWD: was manytomany
	@OneToMany(targetEntity=BaseAssembly.class,
        fetch = javax.persistence.FetchType.LAZY)
	//@JoinTable(name="assemblies", joinColumns={@JoinColumn(name="module_id")},inverseJoinColumns={@JoinColumn(name="assembly_id")})
	//PJWD: @LazyCollection(value=LazyCollectionOption.TRUE)
	public Set getAssemblies() {
		return assemblies;
	}

	public void setAssemblies(Set assemblies) {
		this.assemblies = assemblies;
	}

	/**
	 * @return 19-Apr-2006
	 * name="design_root"
	 * 
	 * @hibernate.many-to-one  column="design_root_id"
	 *                        not-null="true"
	 * 
	 */
        //PJWD: was onetomany
	@OneToOne(fetch=FetchType.LAZY)
	//@ForeignKey(name="design_root_id")
        @JoinColumn(name = "complex_assemby_id")
	public ComplexAssembly getDesignRoot() {
		return designRoot;
	}

	public void setDesignRoot(ComplexAssembly designRoot) {
		this.designRoot = designRoot;
	}

	/**
	 * @return 19-Apr-2006
	 * name="manual"
	 * 
	 * @hibernate.many-to-one  column="manual_id" not-null="true"
	 */
        //PJWD: was onetomany
	@OneToOne(fetch=FetchType.LAZY)
	//@ForeignKey(name="manual_id")
        @JoinColumn(name = "manual_id")
	public Manual getManual() {
		return manual;
	}

	public void setManual(Manual manual) {
		this.manual = manual;
	}

	/*
	 * 
	 * HIBERNATE SPECIFIC
	 * 
	 */

	/**
	 * @return 19-Apr-2006
	 * 
	 * @hibernate.id generator-class="native" column="module_id" 
	 */
	@Id
	@Column(name="module_id")
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	public long getModule_id() {
		return module_id;
	}

	public void setModule_id(long module_id) {
		this.module_id = module_id;
	}

	private int designId = 0;

	private String type = "";

	private long buildDate = 0;

	/**
	 * 
	 * @hibernate.property name="buildDate" column="build_Date"
	 * 
	 */
	public long getBuildDate() {
		return buildDate;
	}

	public void setBuildDate(long buildDate) {
		this.buildDate = buildDate;
	}

	/**
	 * @hibernate.property name="designId" column="design_id" index="module_id_index"
	 * 
	 */
	//@Index(name="module_id_index")
	public int getDesignId() {
		return designId;
	}

	/**
	 * 01-May-2006
	 * 
	 * @param designId
	 *            the designId to set
	 */
	public void setDesignId(int designId) {
		this.designId = designId;
	}

	/**
	 * @hibernate.property name="type" column="type"
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
