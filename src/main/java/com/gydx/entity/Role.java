package com.gydx.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 拽小白
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role implements Serializable {

    private Integer id;
    private String roleName;
    private String description;
    private Integer status;

}
