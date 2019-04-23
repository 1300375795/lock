package com.ydg.cloud.lock.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * @author YDG
 * @description
 * @since 2019-04-22
 */
@Data
public class TestLockDTO implements Serializable {

    /**
     * 主键id
     */
    private Long id;

    private String name;

}
