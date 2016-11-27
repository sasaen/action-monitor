package com.sasaen.monitor.rest;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Rest model representation POJO class. It extends from
 * <code>ResourceSupport</code> in case HATEOAS links were included in the Json
 * response (not the case in this example).
 * 
 * @author sasaen
 *
 */
public class Version extends ResourceSupport {

	private final String content;

	@JsonCreator
	public Version(@JsonProperty("content") String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
}
