package za.co.OO7J;

import java.io.Serializable;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import za.co.OO7J.utils.SettingsUtil;

/**
 * @author pvz 20-Apr-2006
 * 
 * @hibernate.class table="connection" lazy="false" 
 * 
 */
@Entity
@Table(name = "connection")
public class Connection implements Serializable {

    private String type = null;
    private int length;
    private AtomicPart from = null;
    private AtomicPart to = null;

    /**
     *
     * 21-Apr-2006
     *
     * Needed by Hibernate
     */
    public Connection() {
    }

    public Connection(AtomicPart fromPart, AtomicPart toPart) {

        setFrom(fromPart);
        setTo(toPart);

        if (SettingsUtil.debugMode) {
            System.out.println("Connection()-> connection fromId ="
                    + fromPart.getDesignId() + " toId = " + toPart.getDesignId());
        }

        // TODO: PVZ: SET THE TYPE

        // TODO: set the length

        // insert Link to this connection object
        // into appropriate atomic part's "to" list
        // (fromPart->to).add(self);
        // pvz:
        if (fromPart.getToConnections() == null) {
            // the first time
            fromPart.setToConnections(new HashSet());
        }
        fromPart.getToConnections().add(this);
        // Persistence.saveOrUpdate(fromPart);

        // insert Link to this connection object
        // into appropriate atomic part's "from" list
        // (toPart->from).add(self);
        if (toPart.getFromConnections() == null) {
            // the first time
            toPart.setFromConnections(new HashSet());
        }
        toPart.getFromConnections().add(this);
        // Persistence.saveOrUpdate(toPart);

    }

    /**
     * @return 20-Apr-2006
     * name="fromAtomicPart
     *
     * @hibernate.many-to-one " inverse="true"
     *                        class="za.co.OO7J.AtomicPart"
     *                        column="from_atomic_part_id"
     */
    //PJWD: edited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="from_id", referencedColumnName="atomic_part_id")
    //@ForeignKey(name="from_atomic_part_id")
    public AtomicPart getFrom() {
        return from;
    }

    public void setFrom(AtomicPart from) {
        this.from = from;
    }

    @Column(name = "\"length\"")
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return 20-Apr-2006
     *
     * name="toAtomicPart"
     *
     * @hibernate.many-to-one  class="za.co.OO7J.AtomicPart"
     *                        column="to_atomic_part_id"
     */
    //PJWD: edited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="to_id", referencedColumnName="atomic_part_id")
    //@ForeignKey(name="to_atomic_part_id")
    public AtomicPart getTo() {
        return to;
    }

    public void setTo(AtomicPart to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /*
     *
     * HIBERNATE SPECIFIC
     *
     */
    // pvz: hibernate specific
    private long connection_id;

    /**
     * @return 20-Apr-2006
     *
     * @hibernate.id generator-class="native" column="connection_id"
     */
    @Id
    @Column(name = "connection_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public long getConnection_id() {
        return connection_id;
    }

    public void setConnection_id(long connection_id) {
        this.connection_id = connection_id;
    }
}
