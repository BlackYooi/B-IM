package com.black.bim.exception;

/**
 * @description：
 * @author：8568
 */
public class CanNotLoadPropertiesException extends RuntimeException {

    public CanNotLoadPropertiesException(String propertiesName) {
        super(String.format("无法加载配置：%s", propertiesName));
    }
}
