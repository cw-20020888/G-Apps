package com.kcube.cloud.pub;

import java.util.List;

import com.kcube.cloud.error.DefaultException;

public interface AbstractService<T>
{
	public T findItem(T item) throws DefaultException;

	public List<T> findItems(T item) throws DefaultException;

	public int findItemsCnt(T item) throws DefaultException;

	public int insertItem(T item) throws DefaultException;

	public int insertItems(List<T> items) throws DefaultException;

	public int updateItem(T item) throws DefaultException;

	public int updateItems(List<T> items) throws DefaultException;

	public int deleteItem(T item) throws DefaultException;

	public int deleteItems(List<T> items) throws DefaultException;
}
