package za.co.OO7J;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import za.co.OO7J.utils.GeneralParameters;
import za.co.OO7J.utils.NewPersistence;
import za.co.OO7J.utils.RandomUtil;
import za.co.OO7J.utils.SettingsUtil;

/**
 * @author pvz 19-Apr-2006
 * 
 * @hibernate.joined-subclass name="za.co.OO7J.ComplexAssembly"
 *                            table="complex_assembly"
 * 
 * 
 * 
 */
@Entity
@Table(name="complex_assembly")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ComplexAssembly extends Assembly {
	// set of Assembly objects
	private Set subAssemblies = null;

	// pvz: hibernate specific
	private long complex_assembly_id;

	/**
	 * 
	 * 21-Apr-2006
	 * 
	 * Needed by Hibernate
	 */
	public ComplexAssembly() {

	}

	/**
	 * 
	 * In the original code leve == levelNo
	 * 
	 * @param newID
	 * @param module
	 * @param parentAssembly
	 * @param level
	 */
	public ComplexAssembly(int newID, Module module,
			ComplexAssembly parentAssembly, int level) {
		super();
		int nextId = 1;

		setDesignId(newID);
		NewPersistence.save(this);
		// TODO: PVZ: SET THE TYPE
		setType("ComplexAssembly");// is this really needed?
		// TODO: PVZ: SET THE BUILD DATE:
		setBuildDate(GeneralParameters.minAssmDate
				+ (RandomUtil.nextPositiveInt() % (GeneralParameters.maxAssmDate
						- GeneralParameters.minAssmDate + 1)));

		if (SettingsUtil.debugMode) {
			System.out.println("ComplexAssembly() -> newID = " + newID
					+ " levelNo = " + level);
		}

		// initialize the simple stuff (some of it randomly)

		if (parentAssembly != null) {
			setSuperAssembly(parentAssembly);
		}

		int numAssmPerAssm = SettingsUtil.NumAssmPerAssm;
		int numAssmLevels = SettingsUtil.NumAssmLevels;

		if (this.getSubAssemblies() == null) {
			this.setSubAssemblies(new HashSet());
		}

		// recursively create subassemblies for this complex assembly
		for (int i = 0; i < numAssmPerAssm; i++) {
			if (level < numAssmLevels - 1) {
				// create a complex assembly as the subassembly
				nextId = SettingsUtil.nextComplexAssemblyId++;
				ComplexAssembly complexAssembly = new ComplexAssembly(nextId,
						module, this, level + 1);
				NewPersistence.saveOrUpdate(complexAssembly);
				this.subAssemblies.add(complexAssembly);
			} else {
				// create a base assembly as the subassembly
				nextId = SettingsUtil.nextBaseAssemblyId++;
				BaseAssembly baseAssembly = new BaseAssembly(nextId, module,
						this);
				NewPersistence.save(baseAssembly);

				this.getSubAssemblies().add(baseAssembly);

			}// end if-else levelNo
		}// end for

	}

	/**
	 * @return 19-Apr-2006
	 * name="complex_assembly_id"
	 * 
	 * @hibernate.set 
	 * 
	 * @hibernate.key column="ca_sub_assemblies_id"
	 * 
	 * @hibernate.one-to-many class="za.co.OO7J.Assembly"
	 */
	@OneToMany(mappedBy="superAssembly", targetEntity=Assembly.class,fetch=FetchType.LAZY)
	public Set getSubAssemblies() {
		return subAssemblies;
	}

	public void setSubAssemblies(Set subAssemblies) {
		this.subAssemblies = subAssemblies;
	}

	/*
	 * 
	 * HIBERNATE SPECIFIC
	 * 
	 */

}
