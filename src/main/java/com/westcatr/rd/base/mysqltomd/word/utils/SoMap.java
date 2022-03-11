package com.westcatr.rd.base.mysqltomd.word.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : SoMap
 * @Package : com.westcatr.rd.base.mysqltomd.word.utils
 * @Description:
 * @date 2022/3/11 10:00
 **/
public class SoMap extends HashMap<String, Object> {

	public SoMap() { }

	/**
	 * 构造方法，将任意实体类转化为 Map
	 * @param obj
	 */
	public SoMap(Object obj) {
		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		try {
			for (Field field : fields) {
				field.setAccessible(true);
				this.put(field.getName(), field.get(obj));
			}
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	/**
	 * 将 Map 转化为 任意实体类
	 * @param clazz 反射获取类字节码对象
	 * @return
	 */
	public <T> T toEntity(Class<T> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		try {
			Constructor constructor = clazz.getDeclaredConstructor();
			T t = (T) constructor.newInstance();
			for (Field field : fields) {
				field.setAccessible(true);
				field.set(t, this.get(field));
			}
			return t;
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	/**
	 * 从集合中获取一个字段的方法，如果字段不存在返回空
	 * @param key  字段的唯一标识
	 * @param <T>  字段的类型，运行时自动识别，使用时无需声明和强转
	 * @return     对应字段的值
	 */
	public <T> T get(String key) {
		return (T) super.get(key);
	}
}
