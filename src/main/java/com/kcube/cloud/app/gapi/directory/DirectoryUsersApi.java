package com.kcube.cloud.app.gapi.directory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.admin.directory.model.Buildings;
import com.google.api.services.admin.directory.model.CalendarResources;
import com.google.api.services.admin.directory.model.Members;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.Users;
import com.kcube.cloud.app.gapi.CommonGoogleApi;
import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.error.DefaultException;
import com.kcube.cloud.user.User.Building;
import com.kcube.cloud.user.User.Resource;
import com.kcube.cloud.user.User.Resource.Feature;

public class DirectoryUsersApi extends CommonGoogleApi
{
	private static final int[] FIBONACCI = new int[] {1, 1, 2, 3, 5};

	public DirectoryUsersApi(String email, GoogleAuthMethod googleOauth2Method)
	{
		super(email, googleOauth2Method);
	}

	public User get(String userEmail)
	{
		try
		{
			return createDirectoryService()
				.users().get(StringUtils.isNotEmpty(userEmail) ? userEmail : getEmail()).execute();
		}
		// catch (TokenResponseException te)
		// {
		// throw new CommonGoogleTokenException.InvalidRefreshToken(te);
		// }
		catch (GoogleJsonResponseException e)
		{
			if (e.getStatusCode() == 404)
			{
				return null;
			}
			/**
			 * 추후 Group검색 로직개선
			 */
			if (e.getStatusCode() == 400)
			{
				return null;
			}

			throw new DefaultException(e);
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	public Users list(String name, String customer)
	{
		try
		{
			return createDirectoryService()
				.users().list().setQuery("name:" + name).setCustomer(customer).setMaxResults(500)
				.setFields("users(name), users(primaryEmail), users(orgUnitPath)").execute();
			// .users().list().setQuery("name:" +
			// name).setDomain(domain).setMaxResults(500)
			// .setFields("users(name), users(primaryEmail),
			// users(orgUnitPath)").execute();
		}
		// catch (TokenResponseException te)
		// {
		// throw new CommonGoogleTokenException.InvalidRefreshToken(te);
		// }
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	/**
	 * @return {@link List<{@link com.google.api.services.admin.directory.model.Buildings}>}
	 */
	public Buildings getGoogleBuildings(String customer)
	{
		for (int attempt = 0; attempt < FIBONACCI.length; attempt++)
		{
			try
			{
				return createDirectoryService().resources().buildings().list(customer).setMaxResults(500).execute();
			}
			catch (Exception e)
			{
				handleFailure(attempt, e);
			}
		}
		throw new RuntimeException("Failed to getGoogleBuildings.");
	}

	/**
	 * @return {@link List<{@link com.kcube.cloud.user.User.Building}>}
	 */
	public List<Building> getUserBuildings(String customer)
	{
		return getUserBuildings(customer, "");
	}

	public List<Building> getUserBuildings(String customer, String exclusion)
	{
		List<Building> buildings = new ArrayList<Building>();
		List<com.google.api.services.admin.directory.model.Building> gBuildings = getGoogleBuildings(customer)
			.getBuildings();
		for (com.google.api.services.admin.directory.model.Building gBuilding : gBuildings)
		{
			if (StringUtils.isNotEmpty(exclusion) ? exclusion.indexOf(gBuilding.getBuildingId()) < 0 : true)
			{
				Building b = new Building();
				b.setId(gBuilding.getBuildingId());
				b.setName(gBuilding.getBuildingName());
				b.setFloorNames(gBuilding.getFloorNames());

				buildings.add(b);
			}
		}

		return buildings;
	}

	/**
	 * @return {@link List<{@link com.google.api.services.admin.directory.model.CalendarResources}>}
	 */
	public CalendarResources getGoogleResources(String customer)
	{
		for (int attempt = 0; attempt < FIBONACCI.length; attempt++)
		{
			try
			{
				return getGoogleResources(customer, "");
			}
			catch (Exception e)
			{
				handleFailure(attempt, e);
			}
		}
		throw new RuntimeException("Failed to getGoogleResources.");
	}

	/**
	 * @return {@link List<{@link com.google.api.services.admin.directory.model.CalendarResources}>}
	 */
	public CalendarResources getGoogleResources(String customer, String query)
	{
		try
		{
			return createDirectoryService()
				.resources().calendars().list(customer).setMaxResults(500)
				.setOrderBy("buildingId, resourceName, floorName desc").setQuery(query).execute();
		}
		// catch (TokenResponseException te)
		// {
		// throw new CommonGoogleTokenException.InvalidRefreshToken(te);
		// }
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	/**
	 * @return {@link List<{@link com.kcube.cloud.user.User.Resource}>}
	 */
	@SuppressWarnings("unchecked")
	public List<Resource> getUserResources(String customer)
	{
		List<Resource> resources = new ArrayList<Resource>();
		List<com.google.api.services.admin.directory.model.CalendarResource> gResources = getGoogleResources(customer)
			.getItems();
		for (com.google.api.services.admin.directory.model.CalendarResource gResource : gResources)
		{
			if (StringUtils.isNotEmpty(gResource.getBuildingId()))
			{
				Resource r = new Resource();
				r.setId(gResource.getResourceId());
				r.setName(gResource.getResourceName());
				r.setEmail(gResource.getResourceEmail());
				if (gResource.getCapacity() != null)
				{
					r.setCapacity(gResource.getCapacity());
				}
				r.setFloorName(gResource.getFloorName());
				r.setBuildingId(gResource.getBuildingId());

				List<Map<String, Map<String, String>>> gFeatures = (List<Map<String, Map<String, String>>>) gResource
					.getFeatureInstances();

				if (CollectionUtils.isNotEmpty(gFeatures))
				{
					List<Feature> features = new ArrayList<Feature>();

					for (Map<String, Map<String, String>> gFeature : gFeatures)
					{
						Feature feature = new Feature();
						feature.setName(gFeature.get("feature").get("name"));

						features.add(feature);
					}
					r.setFeatures(features);
				}

				resources.add(r);
			}
		}

		return resources;
	}

	public Members getMembersObject(String email)
	{
		try
		{
			return createDirectoryService().members().list(email).execute();
		}
		// catch (TokenResponseException te)
		// {
		// throw new CommonGoogleTokenException.InvalidRefreshToken(te);
		// }
		catch (IOException e)
		{
			return null;
		}
	}

	private void handleFailure(int attempt, Exception e)
	{
		if (e.getCause() != null && !GoogleJsonResponseException.class.equals(e.getCause().getClass()))
		{
			throw new DefaultException(e);
		}
		doWait(attempt);
	}

	private static void doWait(int attempt)
	{
		try
		{
			Thread.sleep(FIBONACCI[attempt] * 1000);
		}
		catch (InterruptedException e)
		{
			throw new DefaultException(e);
		}
	}
}
