package eu.xsdi.mdl.lineagegenerator;

import static org.junit.Assert.*;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class MdlLineageGeneratorTest {
	
	private final String sourceLocalname = "FT1";
	private final String sourceLocalnamePropertyAGeom = "PropertyAGeom";
	private final String sourceNamespace = "http://esdi-humboldt.eu";

	@Test
	public void testGenerateLineage() {
		// build a feature type & feature
		SimpleFeatureType sourcetype = this.getFeatureType(
				this.sourceNamespace, 
				this.sourceLocalname, 
				Polygon.class);
		Feature f = SimpleFeatureBuilder.build(sourcetype, new Object[] {}, "1");
		
		MdlLineageGenerator mdllg = new MdlLineageGenerator();
		mdllg.generateLineage(null, f);
	}
	
	private SimpleFeatureType getFeatureType(
			String featureTypeNamespace, 
			String featureTypeName,  
			Class <? extends Geometry> geom) {
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			ftbuilder.add("geom", geom);
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}

}
