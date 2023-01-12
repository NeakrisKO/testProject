package ru.itb.configuration.DAO.step;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SelectBeforeUpdate;
import ru.itb.configuration.DAO.action.ActionDAO;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity()
@Table(name = "step")
@DynamicUpdate()
@SelectBeforeUpdate()
public class UIStepDAO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "NAME", nullable = false, length = 256)
    private String name;
    @Column(name = "DESCRIPTION", length = 1024)
    private String description;
    @Column(name = "SORT_ORDER", nullable = false)
    private int sortOrder;
    @Column(name = "DELAY")
    private Integer delay;
    @Column(name = "UNIT", length = 64)
    private String unit;
    @Column(name = "TYPE", length = 64, nullable = false)
    private String stepType;
    @OneToMany(cascade = {CascadeType.ALL})
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "step_id")
    @OrderBy(value = "sortOrder ASC ")
    private Set<ActionDAO> action = new HashSet<>();
    @Column(name = "DATE_CREATE")
    private LocalDateTime dateCreate;
    @Column(name = "DATE_UPDATE")
    private LocalDateTime dateUpdate;
    @Column(name = "USER_CREATE")
    private String userCreate;
    @Column(name = "USER_UPDATE")
    private String userUpdate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStepType() {
        return stepType;
    }

    public void setStepType(String stepType) {
        this.stepType = stepType;
    }

    public Set<ActionDAO> getAction() {
        return action;
    }

    public void setAction(Set<ActionDAO> action) {
        this.action = action;
    }

    public LocalDateTime getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(LocalDateTime dateCreate) {
        this.dateCreate = dateCreate;
    }

    public LocalDateTime getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(LocalDateTime dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public String getUserCreate() {
        return userCreate;
    }

    public void setUserCreate(String userCreate) {
        this.userCreate = userCreate;
    }

    public String getUserUpdate() {
        return userUpdate;
    }

    public void setUserUpdate(String userUpdate) {
        this.userUpdate = userUpdate;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof UIStepDAO))
            return false;
        UIStepDAO other = (UIStepDAO) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UIStepDAO [id=" + id + ", name=" + name + "]";
    }
}
