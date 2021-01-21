package com.gydx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 拽小白
 * @createTime 2020-11-11 21:05
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonRecognitionDTO {

    private String text;
    private String choice;

}
