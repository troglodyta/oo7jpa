package za.co.OO7J;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="assembly")
@Inheritance(strategy=InheritanceType.JOINED)

public class Assembly extends DesignObject implements Serializable {
	// needed for hibernate:
	private Long assemblyID;

	// relationship: sub-assemblies
	private ComplexAssembly superAssembly = null;

	// relationship: assemblies
	private Module module = null;

	/**
	 * @return 19-Apr-2006
	 * 
	 * @hibernate.many-to-one column="module_id" not-null="false"
	 */
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="module_id", referencedColumnName="module_id")
	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	/**
	 * @return 19-Apr-2006
	 * 
	 * @hibernate.many-to-one class="za.co.OO7J.ComplexAssembly"
	 *                        column="super_complex_assembly_id"
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	public ComplexAssembly getSuperAssembly() {
		return superAssembly;
	}

	public void setSuperAssembly(ComplexAssembly superAssembly) {
		this.superAssembly = superAssembly;
	}

	/*
	 * 
	 * HIBERNATE SPECIFIC
	 * 
	 */

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
	 * @hibernate.property name="designId" column="design_id" index="assembly_id_index"
	 * 
	 */
	//PJWD @Index(name="assembly_id_index")
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

	/**
	 * @return 19-Apr-2006
	 * 
	 * @hibernate.id generator-class="native" column="assembly_id" 
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	public Long getAssemblyID() {
		return assemblyID;
	}

	public void setAssemblyID(Long assemblyID) {
		this.assemblyID = assemblyID;
	}

}
