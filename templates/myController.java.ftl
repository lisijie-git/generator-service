package ${package.Controller};

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>

import ${package.Entity}.${table.entityName};
import ${package.ServiceImpl}.${table.serviceImplName};
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>

/**
 * 控制层
 * ${table.comment!} 
 *
 * @author ${author}
 * @since ${date}
 */
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>
<#assign injectField="${table.serviceImplName?uncap_first}"/>
	@Autowired
	private ${table.serviceImplName} ${injectField};
	
	/**
	 * 获取实体列表
	 * @return 数据列表
	 */
	@GetMapping("/list")
	public List<${entity}> getList() {
		List<${entity}> list = ${injectField}.getList();
		return list;
	}

	/**
	 * 查询实体,通过id
	 * @param id
	 * @return 实体数据
	 */
	@GetMapping("/get")
	public ${entity} get(@RequestParam(value = "id",required = false) Integer id) {
		${entity} entity = ${injectField}.get(id);
		return entity;
	}

	/**
	 * 查询实体总数
	 * @return 数据总数
	 */
	@GetMapping("/count")
	public Integer getCount() {
		return ${injectField}.getCount();
	}

	/**
	 * 批量插入数据,并返回自增id写进参数对象的列表元素id属性中
	 * @param list 实体列表
	 * @return 一般是影响行数
	 */
	@PostMapping("/add-list")
	public Integer addList(@RequestBody List<${entity}> list) {
		Integer i = ${injectField}.addList(list);
		return i;
	}

	/**
	 * 插入数据,并返回自增id到写进参数对象的id属性中
	 * @param pojo 实体对象
	 * @return 一般是影响行数
	 */
	@PostMapping("/add")
	public Integer add(@RequestBody ${entity} pojo) {
		Integer i = ${injectField}.add(pojo);
		return i;
	}
	
	/**
	 * 修改数据，通过id
	 * @param pojo 实体对象
	 * @return 一般是影响行数
	 */
	@PostMapping("/update")
	public Integer update(@RequestBody ${entity} pojo) {
		Integer i = ${injectField}.update(pojo);
		return i;
	}

	/**
	 * 批量删除数据
	 * @param list id列表
	 * @return 一般是影响行数
	 */
	@PostMapping("/delete-list")
	public Integer deleteList(@RequestBody List<Integer> list) {
		Integer i = ${injectField}.deleteList(list);
		return i;
	}
}
