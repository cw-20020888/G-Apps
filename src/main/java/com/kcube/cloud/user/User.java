package com.kcube.cloud.user;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class User implements Serializable
{
	private static final long serialVersionUID = 1880947532393569728L;

	private String id;
	private String fullName;
	private String primaryEmail;
	private String intraEmail;
	private boolean isGoogleLogin;
	private Location location;
	private List<Building> buildings;
	private List<Resource> resources;
	private List<Long> authorities;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public String getPrimaryEmail()
	{
		return primaryEmail;
	}

	public void setPrimaryEmail(String primaryEmail)
	{
		this.primaryEmail = primaryEmail;
	}

	public String getIntraEmail()
	{
		return intraEmail;
	}

	public void setIntraEmail(String intraEmail)
	{
		this.intraEmail = intraEmail;
	}

	public boolean isGoogleLogin()
	{
		return isGoogleLogin;
	}

	public void setGoogleLogin(boolean isGoogleLogin)
	{
		this.isGoogleLogin = isGoogleLogin;
	}

	public Location getLocation()
	{
		return location;
	}

	public void setLocation(Location location)
	{
		this.location = location;
	}

	public List<Building> getBuildings()
	{
		return buildings;
	}

	public void setBuildings(List<Building> buildings)
	{
		this.buildings = buildings;
	}

	public List<Resource> getResources()
	{
		return resources;
	}

	public void setResources(List<Resource> resources)
	{
		this.resources = resources;
	}

	public List<Long> getAuthorities()
	{
		return authorities;
	}

	public void setAuthorities(List<Long> authorities)
	{
		this.authorities = authorities;
	}

	public static class Building implements Serializable
	{
		private static final long serialVersionUID = -2094984188242312837L;

		private String id;
		private String name;
		private List<String> floorNames;

		public String getId()
		{
			return id;
		}

		public void setId(String id)
		{
			this.id = id;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public List<String> getFloorNames()
		{
			return floorNames;
		}

		public void setFloorNames(List<String> floorNames)
		{
			this.floorNames = floorNames;
		}
	}

	public static class Resource implements Serializable
	{
		private static final long serialVersionUID = 6210134141039174292L;

		private String id;
		private String name;
		private String email;
		private int capacity;
		private String floorName;
		private String buildingId;
		private List<Feature> features;

		public String getId()
		{
			return id;
		}

		public void setId(String id)
		{
			this.id = id;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getEmail()
		{
			return email;
		}

		public void setEmail(String email)
		{
			this.email = email;
		}

		public int getCapacity()
		{
			return capacity;
		}

		public void setCapacity(int capacity)
		{
			this.capacity = capacity;
		}

		public String getFloorName()
		{
			return floorName;
		}

		public void setFloorName(String floorName)
		{
			this.floorName = floorName;
		}

		public String getBuildingId()
		{
			return buildingId;
		}

		public void setBuildingId(String buildingId)
		{
			this.buildingId = buildingId;
		}

		public List<Feature> getFeatures()
		{
			return features;
		}

		public void setFeatures(List<Feature> features)
		{
			this.features = features;
		}

		public static class Feature implements Serializable
		{
			private static final long serialVersionUID = -174242233369096192L;

			private String name;

			public String getName()
			{
				return name;
			}

			public void setName(String name)
			{
				this.name = name;
			}
		}
	}

	public static class Location implements Serializable
	{
		private static final long serialVersionUID = 316305203932790037L;

		private String buildingId;
		private String floorName;

		public String getBuildingId()
		{
			return buildingId;
		}

		public void setBuildingId(String buildingId)
		{
			this.buildingId = buildingId;
		}

		public String getFloorName()
		{
			return floorName;
		}

		public void setFloorName(String floorName)
		{
			this.floorName = floorName;
		}
	}
}
