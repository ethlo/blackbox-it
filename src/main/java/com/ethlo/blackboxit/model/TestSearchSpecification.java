package com.ethlo.blackboxit.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class TestSearchSpecification implements Specification<Test>
{
	private String name;
	private String tag;
    
	@Override
	public Predicate toPredicate(Root<Test> root, CriteriaQuery<?> query, CriteriaBuilder cb)
	{
		final List<Predicate> predicates = new ArrayList<>();
        if (name != null)
        {
        	predicates.add(cb.like(root.get("name"), "%" + name + "%"));
        }
        
        if (tag != null)
        {
        	predicates.add(cb.like(root.get("tags"), "%" + tag + "%"));
        }
        
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}