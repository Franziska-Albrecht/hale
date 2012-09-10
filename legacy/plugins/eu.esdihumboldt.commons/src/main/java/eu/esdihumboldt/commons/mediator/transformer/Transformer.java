//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.01.07 at 01:07:31 PM CET 
//

package eu.esdihumboldt.commons.mediator.transformer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Transformer complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="Transformer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="outputID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="processID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WPSgroundingURI" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DataInput" type="{}DataInput" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Transformer", propOrder = { "id", "outputID", "processID",
		"wpSgroundingURI", "dataInput" })
public class Transformer {

	@XmlElement(name = "ID")
	protected String id;
	@XmlElement(required = true)
	protected String outputID;
	@XmlElement(required = true)
	protected String processID;
	@XmlElement(name = "WPSgroundingURI", required = true)
	protected String wpSgroundingURI;
	@XmlElement(name = "DataInput", required = true)
	protected List<DataInput> dataInput;

	/**
	 * Gets the value of the id property.
	 * 
	 */
	public String getID() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 */
	public void setID(String value) {
		this.id = value;
	}

	/**
	 * Gets the value of the outputID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOutputID() {
		return outputID;
	}

	/**
	 * Sets the value of the outputID property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOutputID(String value) {
		this.outputID = value;
	}

	/**
	 * Gets the value of the processID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProcessID() {
		return processID;
	}

	/**
	 * Sets the value of the processID property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProcessID(String value) {
		this.processID = value;
	}

	/**
	 * Gets the value of the wpSgroundingURI property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getWPSgroundingURI() {
		return wpSgroundingURI;
	}

	/**
	 * Sets the value of the wpSgroundingURI property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setWPSgroundingURI(String value) {
		this.wpSgroundingURI = value;
	}

	/**
	 * Gets the value of the dataInput property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the dataInput property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDataInput().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link DataInput }
	 * 
	 * 
	 */
	public List<DataInput> getDataInput() {
		if (dataInput == null) {
			dataInput = new ArrayList<DataInput>();
		}
		return this.dataInput;
	}

}
