package com.giant.config;

import com.giant.bean.ApplicationStandardSpace;
import com.giant.bean.BeanInformation;
import com.giant.bean.ConstructorArg;
import com.giant.bean.Property;
import com.giant.commons.opeator.FileOperator;
import com.giant.commons.opeator.StringOperator;
import org.dom4j.Element;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApplicationConfiguration {
    private List<BeanInformation> informationList=new ArrayList<>();
    private String config;
    public ApplicationConfiguration(String config) {
        init(config);
        this.config=config;
    }

    public void init(String config) {
        Element root = FileOperator.createRootElement(config);
        //要其必须以beans为根标签
        if(!root.getName().equals(ApplicationStandardSpace.BEANS)){
            return;
        }
        for (Iterator<Element> iterator = root.elementIterator(ApplicationStandardSpace.BEAN); iterator.hasNext(); ) {
            Element beanE = iterator.next();
            BeanInformation information = new BeanInformation();
            String id = beanE.attributeValue(ApplicationStandardSpace.BEAN_ID);
            String className = beanE.attributeValue(ApplicationStandardSpace.BEAN_CLASS_NAME);
            if (id == null || id.equals("")) {
                id = StringOperator.firstToLowerCase(className.substring(className.lastIndexOf(".")+1));
            }
            information.setId(id);
            information.setClassName(className);
            List<ConstructorArg> args= new ArrayList<>();
            for (Iterator<Element> cIt = beanE.elementIterator(ApplicationStandardSpace.BEAN_CONSTRUCTOR_ARG);cIt.hasNext(); ) {
                Element cE = cIt.next();
                ConstructorArg arg = new ConstructorArg();
                arg.setType(cE.attributeValue(ApplicationStandardSpace.CONSTRUCTOR_TYPE));
                arg.setRef(cE.attributeValue(ApplicationStandardSpace.CONSTRUCTOR_REF));
                arg.setValue(cE.attributeValue(ApplicationStandardSpace.CONSTRUCTOR_VALUE));
                args.add(arg);
            }
            information.setArgs(args);
            List<Property> properties = new ArrayList<>();
            for(Iterator<Element> pIt = beanE.elementIterator(ApplicationStandardSpace.BEAN_PROPERTY);pIt.hasNext();){
                Element pE = pIt.next();
                Property property = new Property();
                property.setName(pE.attributeValue(ApplicationStandardSpace.PROPERTY_NAME));
                property.setRef(pE.attributeValue(ApplicationStandardSpace.PROPERTY_REF));
                property.setValue(pE.attributeValue(ApplicationStandardSpace.PROPERTY_VALUE));
                properties.add(property);
            }
            information.setProperties(properties);

            informationList.add(information);
        }
    }


    public List<BeanInformation> getInformationList() {
        return informationList;
    }

    public void setInformationList(List<BeanInformation> informationList) {
        this.informationList = informationList;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
