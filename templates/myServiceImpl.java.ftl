package ${package.ServiceImpl};

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
<#if superServiceImplClassPackage?has_content && superServiceImplClassPackage != "no">
import ${superServiceImplClassPackage};
</#if>

/**
 * service实现类
 * ${table.comment!} 
 *
 * @author ${author}
 * @since ${date}
 */
@Service
public class ${table.serviceImplName} <#if superServiceImplClass != "no">extends ${superServiceImplClass}<${table.mapperName}, ${entity}> </#if>implements ${table.serviceName} {
<#assign injectField="${table.mapperName?uncap_first}"/>
	@Autowired
	private ${table.mapperName} ${injectField};

	@Override
	public List<${entity}> getList() {
		List<${entity}> list = ${injectField}.getList();
		return list;
	}

	@Override
	public ${entity} get(Integer id) {
		${entity} entity = ${injectField}.get(id);
		return entity;
	}

	@Override
	public Integer getCount() {
		return ${injectField}.getCount();
	}

	@Override
	@Transactional
	public Integer addList(List<${entity}> list) {
		Integer i = ${injectField}.addList(list);
		return i;
	}

	@Override
	@Transactional
	public Integer add(${entity} pojo) {
		Integer i = ${injectField}.add(pojo);
		return i;
	}

	@Override
	@Transactional
	public Integer update(${entity} pojo) {
		Integer i = ${injectField}.update(pojo);
		return i;
	}

	@Override
	@Transactional
	public Integer deleteList(List<Integer> list) {
		Integer i = ${injectField}.deleteList(list);
		return i;
	}
}
