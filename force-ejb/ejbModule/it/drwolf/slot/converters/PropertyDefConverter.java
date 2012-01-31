package it.drwolf.slot.converters;

import it.drwolf.slot.entity.PropertyDef;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("propertyDefConverter")
@org.jboss.seam.annotations.faces.Converter(id = "propertyDefConverter")
@BypassInterceptors
public class PropertyDefConverter implements Converter {

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		PropertyDef propertyDef = null;
		String[] split = arg2.split(":");
		String _id = split[0];
		if (!_id.equals("")) {
			EntityManager entityManager = (EntityManager) org.jboss.seam.Component
					.getInstance("entityManager");
			Long id = new Long(_id);
			propertyDef = entityManager.find(PropertyDef.class, id);
		} else {
			// SlotDefEditBean slotDefEditBean = (SlotDefEditBean)
			// org.jboss.seam.Component
			// .getInstance("slotDefEditBean");
			// Map<String, PropertyDef> converterPropertyMap = slotDefEditBean
			// .getConverterPropertyMap();
			// String uuid = split[1];
			// propertyDef = converterPropertyMap.get(uuid);

			PropertyDefConverterBean propertyDefConverterBean = (PropertyDefConverterBean) org.jboss.seam.Component
					.getInstance("propertyDefConverterBean");
			Map<String, PropertyDef> converterPropertyMap = propertyDefConverterBean
					.getMap();
			String uuid = split[1];
			propertyDef = converterPropertyMap.get(uuid);
		}
		return propertyDef;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		// TODO Auto-generated method stub
		return arg2.toString();
	}

}
