package com.kcube.cloud.app.gapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.StructuredQuery.Filter;
import com.kcube.cloud.error.DefaultException;
import com.kcube.cloud.pub.enumer.DataStoreKindEnum;

public class CommonGoogleDataStoreApi
{
	private WebApplicationContext cc = ContextLoader.getCurrentWebApplicationContext();
	private Properties property = cc.getBean("appProp", Properties.class);

	private Datastore datastore = createDataStoreSerivce();
	private DataStoreKindEnum kind;
	private KeyFactory keyFactory;

	public CommonGoogleDataStoreApi(DataStoreKindEnum kind)
	{
		this.kind = kind;
		this.keyFactory = datastore.newKeyFactory().setKind(kind.value());
	}

	private Datastore createDataStoreSerivce()
	{
		Datastore ds = null;
		try
		{
			ds = DatastoreOptions
				.newBuilder().setProjectId(property.getProperty("google.projectId"))
				.setCredentials(
					ServiceAccountCredentials.fromStream(
						new FileInputStream(
							new File(
								this
									.getClass().getResource(property.getProperty("google.serviceAccount.jsonFile"))
									.toURI()))))
				.build().getService();
		}
		catch (IOException | URISyntaxException e)
		{
			throw new DefaultException(e);
		}
		return ds;
	}

	public Key newKey(String keyName)
	{
		return StringUtils.isNotEmpty(keyName) ? keyFactory.newKey(keyName) : (Key) keyFactory.newKey();
	}

	public Entity get(String keyName)
	{
		return datastore.get(newKey(keyName));
	}

	public void delete(String keyName)
	{
		datastore.delete(newKey(keyName));
	}

	public Entity put(Entity entity)
	{
		return datastore.put(entity);
	}

	public List<Entity> put(Entity... entities)
	{
		return datastore.put(entities);
	}

	public void update(Entity... entities)
	{
		datastore.update(entities);
	}

	/**
	 * @param filter see com.google.cloud.datastore.StructuredQuery.CompositeFilter,
	 *        com.google.cloud.datastore.StructuredQuery.PropertyFilter
	 * @param orderDescProperty
	 * @param startCursorString
	 * @param limit
	 * @return
	 */
	public Map<String, Object> run(Filter filter, String orderDescProperty, String startCursorString, int limit)
	{
		Cursor startCursor = null;
		if (StringUtils.isNotEmpty(startCursorString))
		{
			startCursor = Cursor.fromUrlSafe(startCursorString);
		}

		EntityQuery.Builder builder = Query.newEntityQueryBuilder().setKind(kind.value());
		if (filter != null)
		{
			builder.setFilter(filter);
		}
		if (StringUtils.isNotEmpty(orderDescProperty))
		{
			builder.setOrderBy(StructuredQuery.OrderBy.desc(orderDescProperty));
		}
		if (limit > 0)
		{
			builder.setLimit(limit);
		}
		builder.setStartCursor(startCursor);

		QueryResults<Entity> queryResult = datastore.run(builder.build());
		List<Entity> listResult = new ArrayList<Entity>();
		while (queryResult.hasNext())
		{
			listResult.add(queryResult.next());
		}
		Cursor cursor = queryResult.getCursorAfter();

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("listResult", listResult);
		result.put("startCursor", cursor != null ? cursor.toUrlSafe() : null);

		return result;
	}
}
