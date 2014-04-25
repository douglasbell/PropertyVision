package com.dugbel.glass.propertyvision;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;

import android.location.Location;

/**
 * Object holder for the {@link ZillowLookupActivity} lookup response
 * 
 * @author Doug Bell (douglas.bell@gmail.com)
 * 
 */
public class PropertyDetail implements Serializable {

	/** serial version UID */
	private static final long serialVersionUID = 1L;

	/** The address */
	private String address;

	/** The number of bathrooms */
	private Float bathrooms;

	/** The number of bedrooms */
	private Integer bedrooms;

	/** The city */
	private String city;

	/** The currency prices are represented in */
	private Currency currency = Currency.getInstance(Locale.getDefault());

	/** URL containing more detail on the property */ 
	private String detailUrl;

	/** The high Zestimate for this property */
	private BigDecimal highValuation;

	/** The last date this property was sold */
	private DateTime lastSoldDate;

	/** The last price the property was sold for */
	private BigDecimal lastSoldPrice;

	/** Instance of {@link Location} containing the property location details */
	private Location location;

	/** The total square footage of the properties lot */
	private Float lotSizeSqFt;

	/** The low Zestimate for this property */
	private BigDecimal lowValuation;

	/** The postal code */
	private String postalCode;

	/** The region */
	private String region;

	/** The total livable square footage */
	private Float totalSqFt;

	/** The estimate for the price of this property (the Zestimate) */
	private BigDecimal valuation;

	/** The year the property was built */
	private Integer yearBuilt;
	
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return the bathrooms
	 */
	public Float getBathrooms() {
		return bathrooms;
	}

	/**
	 * @return the bedrooms
	 */
	public Integer getBedrooms() {
		return bedrooms;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @return the detailUrl
	 */
	public String getDetailUrl() {
		return detailUrl;
	}

	/**
	 * @return the highValuation
	 */
	public BigDecimal getHighValuation() {
		return highValuation;
	}

	/**
	 * @return the lastSoldDate
	 */
	public DateTime getLastSoldDate() {
		return lastSoldDate;
	}

	/**
	 * @return the lastSoldPrice
	 */
	public BigDecimal getLastSoldPrice() {
		return lastSoldPrice;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @return the lotSizeSqFt
	 */
	public Float getLotSizeSqFt() {
		return lotSizeSqFt;
	}

	/**
	 * @return the lowValuation
	 */
	public BigDecimal getLowValuation() {
		return lowValuation;
	}

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @return the totalSqFt
	 */
	public Float getTotalSqFt() {
		return totalSqFt;
	}

	/**
	 * @return the valuation
	 */
	public BigDecimal getValuation() {
		return valuation;
	}

	/**
	 * @return the yearBuilt
	 */
	public Integer getYearBuilt() {
		return yearBuilt;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @param bathrooms
	 *            the bathrooms to set
	 */
	public void setBathrooms(Float bathrooms) {
		this.bathrooms = bathrooms;
	}

	/**
	 * @param bedrooms
	 *            the bedrooms to set
	 */
	public void setBedrooms(Integer bedrooms) {
		this.bedrooms = bedrooms;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @param detailUrl the detailUrl to set
	 */
	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	/**
	 * @param highValuation
	 *            the highValuation to set
	 */
	public void setHighValuation(BigDecimal highValuation) {
		this.highValuation = highValuation;
	}

	/**
	 * @param lastSoldDate
	 *            the lastSoldDate to set
	 */
	public void setLastSoldDate(DateTime lastSoldDate) {
		this.lastSoldDate = lastSoldDate;
	}

	/**
	 * @param lastSoldPrice
	 *            the lastSoldPrice to set
	 */
	public void setLastSoldPrice(BigDecimal lastSoldPrice) {
		this.lastSoldPrice = lastSoldPrice;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @param lotSizeSqFt
	 *            the lotSizeSqFt to set
	 */
	public void setLotSizeSqFt(Float lotSizeSqFt) {
		this.lotSizeSqFt = lotSizeSqFt;
	}

	/**
	 * @param lowValuation
	 *            the lowValuation to set
	 */
	public void setLowValuation(BigDecimal lowValuation) {
		this.lowValuation = lowValuation;
	}

	/**
	 * @param postalCode
	 *            the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * @param region
	 *            the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * @param totalSqFt
	 *            the totalSqFt to set
	 */
	public void setTotalSqFt(Float totalSqFt) {
		this.totalSqFt = totalSqFt;
	}

	/**
	 * @param valuation
	 *            the valuation to set
	 */
	public void setValuation(BigDecimal valuation) {
		this.valuation = valuation;
	}

	/**
	 * @param yearBuilt
	 *            the yearBuilt to set
	 */
	public void setYearBuilt(Integer yearBuilt) {
		this.yearBuilt = yearBuilt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

}