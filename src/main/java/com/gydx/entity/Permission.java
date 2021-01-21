package com.gydx.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author 拽小白
 * @createTime 2020-11-14 14:18
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission implements Serializable {

    private Integer id;
    private String permissionName;
    private String title;
    private String index;
    private String type;
    private String icon;
    private String description;
    private Integer parentId;
    private Boolean available;
    private List<Permission> children;

}
