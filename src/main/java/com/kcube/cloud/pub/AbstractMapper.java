package com.kcube.cloud.pub;

import java.util.List;

import com.kcube.cloud.pub.anno.DefaultMapper;

@DefaultMapper
public interface AbstractMapper<T>
{
	public T findItem(T item);

	public List<T> findItems(T item);

	public int findItemsCnt(T item);

	public int insertItem(T item);

	public int updateItem(T item);

	public int deleteItem(T item);
}
