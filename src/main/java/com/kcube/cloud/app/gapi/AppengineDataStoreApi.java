package com.kcube.cloud.app.gapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.kcube.cloud.pub.enumer.DataStoreKindEnum;

public class AppengineDataStoreApi
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private Key key;

	public AppengineDataStoreApi(DataStoreKindEnum kind)
	{
		this(kind, new Date().getTime());
	}

	public AppengineDataStoreApi(DataStoreKindEnum kind, long keyId)
	{
		key = KeyFactory.createKey(kind.value(), keyId);
	}

	public AppengineDataStoreApi(DataStoreKindEnum kind, String keyName)
	{
		key = KeyFactory.createKey(kind.value(), keyName);
	}

	public Key put(Map<String, String> fields)
	{
		Entity entity = new Entity(key);
		for (String field : fields.keySet())
		{
			entity.setProperty(field, fields.get(field));
		}
		return datastore.put(entity);
	}

	public Entity get()
	{
		Entity entity = null;
		try
		{
			entity = datastore.get(key);
		}
		catch (EntityNotFoundException e)
		{
			logger.error("Appengine Datastore EntityNotFoundException!!");
		}
		return entity;
	}

	public void delete()
	{
		datastore.delete(key);
	}

	public Map<String, Object> run(Query.Filter filter, String orderDescProperty, String startCursorString, int limit)
	{
		Cursor startCursor = null;
		if (StringUtils.isNotEmpty(startCursorString))
		{
			startCursor = Cursor.fromWebSafeString(startCursorString);
		}

		Query builder = new Query(key.getKind());
		if (filter != null)
		{
			builder.setFilter(filter);
		}
		if (StringUtils.isNotEmpty(orderDescProperty))
		{
			builder.addSort(orderDescProperty, Query.SortDirection.DESCENDING);
		}

		FetchOptions options = FetchOptions.Builder.withDefaults();
		if (limit > 0)
		{
			options.limit(limit);
		}
		options.startCursor(startCursor);

		PreparedQuery query = datastore.prepare(builder);
		QueryResultList<Entity> queryResult = query.asQueryResultList(options);
		List<Entity> listResult = new ArrayList<Entity>();
		for (Entity result : queryResult)
		{
			listResult.add(result);
		}
		Cursor cursor = queryResult.getCursor();

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("listResult", listResult);
		result.put("startCursor", cursor != null ? cursor.toWebSafeString() : null);

		return result;
	}
}
