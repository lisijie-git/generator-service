package ${package.Service};

import java.util.List;

import ${package.Entity}.${entity};
<#if superServiceClassPackage?has_content && superServiceClassPackage != "no">
import ${superServiceClassPackage};
</#if>

/**
 * service接口
 * ${table.comment!} 
 *
 * @author ${author}
 * @since ${date}
 */
public interface ${table.serviceName} <#if superServiceClass != "no">extends ${superServiceClass}<${entity}> </#if>{
	/**
	 * 获取实体列表
	 * @return 数据列表
	 */
	List<${entity}> getList();

	/**
	 * 查询实体,通过id
	 * @param id
	 * @return 实体数据
	 */
	${entity} get(Integer id);

	/**
	 * 查询实体总数
	 * @return 数据总数
	 */
	Integer getCount();

	/**
	 * 批量插入数据,并返回自增id写进参数对象的列表元素id属性中
	 * @param list 实体列表
	 * @return 一般是影响行数
	 */
	Integer addList(List<${entity}> list);

	/**
	 * 插入数据,并返回自增id到写进参数对象的id属性中
	 * @param pojo 实体对象
	 * @return 一般是影响行数
	 */
	Integer add(${entity} pojo);

	/**
	 * 修改数据，通过id
	 * @param pojo 实体对象
	 * @return 一般是影响行数
	 */
	Integer update(${entity} pojo);

	/**
	 * 批量删除数据
	 * @param list id列表
	 * @return 一般是影响行数
	 */
	Integer deleteList(List<Integer> list);
}
