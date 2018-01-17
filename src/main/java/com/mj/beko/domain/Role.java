package com.mj.beko.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Ricardo on 2017/8/15.
 */
@Entity
@Table(name = "t_role")
@Data
public class Role implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "role_no")
    private String roleNo;

    @Column(name = "role_desc")
    private String roleDesc;

    /*添加用户和角色的多对多关系*/
    @ManyToMany
    @JsonIgnore
    public Set<User> users = new HashSet<User>();

    /*添加角色和menu*/
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_menu", joinColumns = {
            @JoinColumn(name = "role_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "menu_id", referencedColumnName = "id")})
    private Set<Menu> menus;
    public Role(){}
    public Role(String name,String roleNo,String roleDesc){
        this.name=name;
        this.roleNo=roleNo;
        this.roleDesc=roleDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Role role = (Role) o;
        if (role.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), role.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                ", roleNo='" + getRoleNo() + "'" +
                "}";
   }
}
