package com.giant.config;

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
        for (Iterator<Element> iterator = root.elementIterator("bean"); iterator.hasNext(); ) {
            Element beanE = iterator.next();
            BeanInformation information = new BeanInformation();
            String id = beanE.attributeValue("id");
            String className = beanE.attributeValue("class");
            if (id == null || id.equals("")) {
                id = StringOperator.firstToLowerCase(className.substring(className.lastIndexOf(".")+1));
            }
            information.setId(id);
            information.setClassName(className);
            information.setFactoryMethod(beanE.attributeValue("factory.method"));
            List<ConstructorArg> args= new ArrayList<>();
            for (Iterator<Element> cIt = beanE.elementIterator("constructor-arg"); cIt.hasNext(); ) {
                Element cE = cIt.next();
                ConstructorArg arg = new ConstructorArg();
                arg.setType(cE.attributeValue("type"));
                arg.setRef(cE.attributeValue("ref"));
                arg.setValue(cE.attributeValue("value"));
                args.add(arg);
            }
            information.setArgs(args);
            List<Property> properties = new ArrayList<>();
            for(Iterator<Element> pIt = beanE.elementIterator("property");pIt.hasNext();){
                Element pE = pIt.next();
                Property property = new Property();
                property.setName(pE.attributeValue("name"));
                property.setRef(pE.attributeValue("ref"));
                property.setValue(pE.attributeValue("value"));
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
