<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${package.Mapper}.${table.mapperName}">

	<sql id="${table.name}">
		${table.name}
	</sql>

<#if baseResultMap>
	<!-- 通用查询映射结果 -->
	<resultMap id="ResultMap" type="${package.Entity}.${entity}">
<#list table.fields as field>
<#if field.keyFlag><#--生成主键排在第一位-->
		<id column="${field.name}" property="${field.propertyName}" />
		<#assign tId="${(field.name)!'id'}"/><#--freemarker声明模板变量，表id名-->
		<#assign eId="${(field.propertyName)!'id'}"/><#--freemarker声明模板变量，实体id名-->
<#else>
		<#assign tId="id"/>
		<#assign eId="id"/>
</#if>
</#list>
<#list table.commonFields as field><#--生成公共字段 -->
	<result column="${field.name}" property="${field.propertyName}" />
</#list>
<#list table.fields as field>
<#if !field.keyFlag><#--生成普通字段 -->
		<result column="${field.name}" property="${field.propertyName}" />
</#if>
</#list>
	</resultMap>

</#if>
<#if baseColumnList>
	<!-- 通用查询结果列 -->
	<sql id="t_columns">
<#list table.commonFields as field>
		${field.name},
</#list>
<#list table.fields as field>
<#if field_has_next>
		${field.name},
<#else>
		${field.name}
</#if>
</#list>
	</sql>
</#if>

	<!-- 查询实体列表 -->
	<select id="getList" resultType="${package.Entity}.${entity}">
		SELECT 
			<include refid="t_columns"/> 
		FROM 
			<include refid="${table.name}"/> 
	</select>
	
	<!-- 查询实体,通过id -->
	<select id="get" resultType="${package.Entity}.${entity}">
		SELECT 
			<include refid="t_columns"/> 
		FROM 
			<include refid="${table.name}"/> 
		WHERE
			${tId} = <#noparse>#</#noparse>{id}
	</select>
	
	<!-- 查询实体总数 -->
	<select id="getCount" resultType="java.lang.Integer">
		SELECT 
			COUNT(*) 
		FROM <include refid="${table.name}"/> 
		WHERE state = 1
	</select>
	
	<!-- 批量插入数据,并返回自增id写进参数对象的列表元素id属性中。 -->
	<!-- useGeneratedKeys需要数据库有自增支持，useGeneratedKeys插入多条数据就会返回多条id。如mysql等。oracle不支持要其他方法获取。 -->
	<!-- mysql中使用selectKey标签并使用SELECT LAST_INSERT_ID()只会返回一个id，相当于嵌入一个查询最后插入操作的id的语句。 -->
	<insert id="addList" parameterType="${package.Entity}.${entity}" 
		keyProperty="${eId}" keyColumn="${tId}" useGeneratedKeys="true">
		INSERT INTO <include refid="${table.name}"/> 
		<trim prefix="(" suffix=")" suffixOverrides=",">
<#list table.fields as field>
<#if !field.keyFlag>
			${field.name},
<#else>
			<!-- ${field.name}, -->
</#if>
</#list>
		</trim> 
		VALUES 
		<foreach collection="list" item="pojo" index="index" open="(" separator="," close=")" >
<#list table.fields as field>
<#if !field.keyFlag>
			<#noparse>#</#noparse>{pojo.${field.propertyName}},
<#else>
			<!-- <#noparse>#</#noparse>{pojo.${field.propertyName}}, -->
</#if>
</#list>
		</foreach>
	</insert>
	
	<!-- 插入数据,并返回自增id写进参数对象的id属性中。 -->
	<!-- 不支持自增主键的数据库useGeneratedKeys=true会出错，需要用selectKey来实现。 -->
	<!-- mysql也可以用selectKey，用SELECT LAST_INSERT_ID()方法实现。 -->
	<!-- 但SELECT LAST_INSERT_ID()只会返回一个id，在connection中，并发操作有可能出现id是其他插入生成的id -->
	<insert id="add">
		<selectKey keyProperty="pojo.${eId}" order="AFTER" resultType="java.lang.Integer">
			SELECT LAST_INSERT_ID()
		</selectKey>
		INSERT INTO <include refid="${table.name}"/> 
		<trim prefix="(" suffix=")" suffixOverrides=",">
<#list table.fields as field>
<#if !field.keyFlag>
			<if test="pojo.${field.propertyName} != null">${field.name},</if>
<#else>
			<!-- <if test="pojo.${field.propertyName} != null">${field.name},</if> -->
</#if>
</#list>
		</trim> 
		VALUES 
		<trim prefix="(" suffix=")" suffixOverrides=",">
<#list table.fields as field>
<#if !field.keyFlag>
			<if test="pojo.${field.propertyName} != null"><#noparse>#</#noparse>{pojo.${field.propertyName}},</if>
<#else>
			<!-- <if test="pojo.${field.propertyName} != null"><#noparse>#</#noparse>{pojo.${field.propertyName}},</if> -->
</#if>
</#list>
		</trim>
	</insert>
	
	<!-- 修改数据，通过id -->
	<update id="update">
		UPDATE <include refid="${table.name}"/> 
		<set>
<#list table.fields as field>
			<if test="pojo.${field.propertyName} != null">${field.name} = <#noparse>#</#noparse>{pojo.${field.propertyName}},</if>
</#list>
		</set>
		WHERE ${tId} = <#noparse>#</#noparse>{pojo.${eId}}
	</update>
	
	<!-- 批量删除数据，通过id列表 -->
	<delete id="deleteList">
		DELETE FROM <include refid="${table.name}"/> 
		WHERE ${tId} IN 
		<foreach collection="list" item="item" index="index" open="(" separator="," close=")">
			<#noparse>#</#noparse>{item}
		</foreach>
	</delete>
	
</mapper>
