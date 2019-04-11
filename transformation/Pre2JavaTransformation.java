package magicUWE.transformation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Constraint;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Generalization;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralBoolean;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralInteger;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralString;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralUnlimitedNatural;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.MultiplicityElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TypedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ValueSpecification;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Image;

import magicUWE.shared.MessageWriter;
import magicUWE.stereotypes.UWEStereotypeClassNav;
import magicUWE.stereotypes.UWEStereotypeClassPres;

import magicUWE.shared.MessageWriter;
import org.apache.log4j.Logger;

abstract class JSPNode{
	public String name = "";
	public String value = "";
	protected JSPNode(){
	}
	protected JSPNode(String name){
		this.name = name;
	}
	protected JSPNode(String name, String value){
		this.name = name;
		this.value = value;
	}
	public String toCode(){
		return "";
	}
}

class JSPAttribute extends JSPNode{
	public JSPAttribute(String name) {
		// TODO Auto-generated constructor stub
		super(name);
	}
	public JSPAttribute(String name, String value){
		super(name, value);
	}
	@Override
	public String toCode(){
		return this.name + "=\"" + this.value + "\"";
	}
}

class JSPTextNode extends JSPNode{
	public JSPTextNode(String value){
		this.value = value;
	}
	@Override
	public String toCode(){
		return this.value + "\n";
	}
}

class JSPElement extends JSPNode{
	public JSPElement() {
	}
	public JSPElement(String name) {
		super(name);
	}
	public List<JSPNode> children = new ArrayList<JSPNode>();
	public List<JSPAttribute> getAttribute(){		
			List<JSPAttribute> lst = new ArrayList<JSPAttribute>();
			for (JSPNode node : children) {
				if(node instanceof JSPAttribute)
					lst.add((JSPAttribute) node);
			}
			return lst;		
	}
	public List<JSPNode> getChildren(){		
			List<JSPNode> lst = new ArrayList<JSPNode>();
			for (JSPNode node : children) {
				if(!(node instanceof JSPAttribute))
				{
					if(node instanceof JSPTextNode)
						lst.add((JSPTextNode)node);
					else
						lst.add(node);
				}
			}
			return lst;		
	}
	@Override
	public String toCode(){
		String content = "";		
		content += "<" + this.name;
		for (JSPAttribute itAttr : getAttribute()) {
			content += " " + itAttr.toCode();
		}
		content += ">\n";
		for (JSPNode node : getChildren()) {
			content += node.toCode();
		}
		content += "</" + this.name + ">\n";
		return content;
	}
}

class JSPRoot extends JSPElement{
	public String documentName = "";
}

public class Pre2JavaTransformation {
	public static void Presentation2JSPTransformation(ArrayList<NamedElement> listClass)
	{
		for (NamedElement c : listClass) {
			if (helper_hasStereoType(c, UWEStereotypeClassPres.PRESENTATION_PAGE)){
				Presentation2JSP(c);
			}				
		}

	}
	
	public static void Presentation2JSP(NamedElement r){
		JSPRoot jspRoot = new JSPRoot();
		jspRoot.documentName = r.getName() + ".jsp";
		if(helper_hasStereoType(r, UWEStereotypeClassPres.PRESENTATION_PAGE)){
			JSPElement htmlNode = new JSPElement("html"), 
					headNode 	= new JSPElement("head"), 
					bodyNode 	= new JSPElement("body"), 
					titleNode 	= new JSPElement("title");
			titleNode.children.add(new JSPTextNode(r.getName()));
			headNode.children.add(titleNode);
			htmlNode.children.add(headNode);
			htmlNode.children.add(bodyNode);
			PreClass2JSP(bodyNode, r);
			jspRoot.children.add(htmlNode);
		}
		helper_write2File(jspRoot);
	}
	
	public static void PreClass2JSP(JSPElement owner, NamedElement e){
		if(e.getName().isEmpty()) return;
		if (helper_hasStereoType(e, UWEStereotypeClassPres.PRESENTATION_PAGE)) {
			JSPElement capNodeEl = new JSPElement("h2");
			capNodeEl.children.add(new JSPTextNode(e.getName()));
			JSPElement bodyEl = new JSPElement("div");
			// get childrent
			Iterator<Element> itElements = e.getOwnedElement().iterator();
			while (itElements.hasNext()) {
				NamedElement c = (NamedElement) itElements.next();
				PreClass2JSP(bodyEl, c);
			}
			owner.children.addAll(Arrays.asList(capNodeEl, bodyEl));
		} else if (helper_hasStereoType(e, UWEStereotypeClassPres.PRESENTATION_GROUP)) {
			JSPElement bodyEl = new JSPElement("div");
			if (helper_isRelativeToIndex(e)) {
				JSPElement ulEl = new JSPElement("ul");
				JSPElement foreachEl = new JSPElement("c:forEach");
				foreachEl.children.addAll(Arrays.asList(new JSPAttribute("items", "${list" + e.getName() + "}"),
						new JSPAttribute("var", e.getName())));
				JSPElement liEl = new JSPElement("li");
				JSPElement cifEl = new JSPElement("c:if");
				cifEl.children.add(new JSPAttribute("test", "${not empty " + e.getName() + "}"));

				// get childrent
				Iterator<Element> itElements = e.getOwnedElement().iterator();
				while (itElements.hasNext()) {
					NamedElement c = (NamedElement) itElements.next();
					PreClass2JSP(cifEl, c);
				}
				liEl.children.add(cifEl);
				foreachEl.children.add(liEl);
				ulEl.children.add(foreachEl);
				bodyEl.children.add(ulEl);
			} else {
				// get childrent
				Iterator<Element> itElements = e.getOwnedElement().iterator();
				while (itElements.hasNext()) {
					NamedElement c = (NamedElement) itElements.next();
					PreClass2JSP(bodyEl, c);
				}
			}
			owner.children.add(bodyEl);
		} else if (helper_hasStereoType(e, UWEStereotypeClassPres.PRESENTATION_ALTERNATIVES)) {
			JSPElement bodyEl = new JSPElement("c:choose");
			// get childrent
			Iterator<Element> itElements = e.getOwnedElement().iterator();
			while (itElements.hasNext()) {
				NamedElement c = (NamedElement) itElements.next();
				if (!c.getName().isEmpty()) {
					JSPElement cwhenEl = new JSPElement("c:when");
					JSPAttribute testEl = new JSPAttribute("test");
					testEl.value = "${" + e.getName() + "=='" + c.getName() + "'}";
					PreClass2JSP(cwhenEl, c);
					cwhenEl.children.add(testEl);
					bodyEl.children.add(cwhenEl);

				}
			}
			owner.children.add(bodyEl);
		} else if (helper_hasStereoType(e, UWEStereotypeClassPres.INPUT_FORM)) {
			JSPElement bodyEl = new JSPElement("div");			
			JSPElement inputFormEl = new JSPElement("form");
			inputFormEl.children.addAll(Arrays.asList(new JSPAttribute("action", e.getName()), new JSPAttribute("method", "post")));
			JSPElement tbEL = new JSPElement("table");
			// get childrent
			Iterator<Element> itElements = e.getOwnedElement().iterator();
			while (itElements.hasNext()) {
				NamedElement c = (NamedElement) itElements.next();
				if (c.getName().isEmpty())
					continue;
				JSPElement trEl = new JSPElement("tr");
				PreClass2JSP(trEl, c);
				tbEL.children.add(trEl);
			}	
			inputFormEl.children.add(tbEL);
			bodyEl.children.add(inputFormEl);
			owner.children.add(bodyEl);
		} else
			PreElement2JSP(owner, e);
	}
	
	public static void PreElement2JSP(JSPElement owner, NamedElement e){
		// text2jsp
		if(e.getName().isEmpty()) return;
		if (helper_hasStereoType(e, UWEStereotypeClassPres.TEXT)) {
			JSPElement cOutEl = new JSPElement("c:out");
			JSPAttribute valueAttr = new JSPAttribute("value");
			valueAttr.value = "${" + ((NamedElement) e.getOwner()).getName() + ".get" + e.getName() + "()" + "}";
			cOutEl.children.add(valueAttr);
			owner.children.add(cOutEl);
		} else if (helper_hasStereoType(e, UWEStereotypeClassPres.IMAGE)) {
			JSPElement imgEl = new JSPElement("img");
			JSPAttribute srcAttr = new JSPAttribute("src", "#");
			JSPAttribute atlAttr = new JSPAttribute("alt", "#");
			imgEl.children.addAll(Arrays.asList(srcAttr, atlAttr));
			owner.children.add(imgEl);
		} else if (helper_hasStereoType(e, UWEStereotypeClassPres.BUTTON)) {
			JSPElement formEl = new JSPElement("form");
			JSPAttribute actAttr = new JSPAttribute("action", e.getName());
			JSPAttribute methAttr = new JSPAttribute("method", "post");
			
			JSPElement inputEl = new JSPElement("input");
			JSPAttribute typeAttr = new JSPAttribute("type", "submit");
			JSPAttribute valAttr = new JSPAttribute("value", e.getName().toLowerCase());
			JSPAttribute nameAttr = new JSPAttribute("name", e.getName());
			inputEl.children.addAll(Arrays.asList(typeAttr, valAttr, nameAttr));
			
			formEl.children.addAll(Arrays.asList(actAttr, methAttr, inputEl));	
			owner.children.add(formEl);
		} else if (helper_hasStereoType(e, UWEStereotypeClassPres.ANCHOR)){
			JSPElement anchorEl = new JSPElement("a");
			JSPAttribute hrefAttr = new JSPAttribute("href", "#");
			JSPTextNode linkText = new JSPTextNode(e.getName());
			anchorEl.children.addAll(Arrays.asList(hrefAttr, linkText));
			owner.children.add(anchorEl);
		}else if (helper_hasStereoType(e, UWEStereotypeClassPres.TEXT_INPUT)) {
			JSPElement textInputEl = new JSPElement("p");
			JSPTextNode textNode = new JSPTextNode("Enter " + e.getName() + " : ");
			JSPElement inputEl = new JSPElement("input");
			inputEl.children.addAll(Arrays.asList(new JSPAttribute("type", "text"), new JSPAttribute("name", e.getName()), new JSPAttribute("value", "")));
			textInputEl.children.addAll(Arrays.asList(textNode, inputEl));
			owner.children.add(textInputEl);
		} else if (helper_hasStereoType(e, UWEStereotypeClassPres.FILE_UPLOAD)) {		
			JSPElement formEl = new JSPElement("form");
			JSPAttribute actAttr = new JSPAttribute("action", "upload");
			JSPAttribute methAttr = new JSPAttribute("method", "post");
			JSPAttribute enctypeAttr = new JSPAttribute ("enctype", "multipart/form-data");
			
			JSPElement fileInputEl = new JSPElement("input");
			fileInputEl.children.addAll(Arrays.asList( new JSPAttribute("type", "file"),new JSPAttribute("name", "file") ));
			JSPElement inputEl = new JSPElement("input");
			inputEl.children.addAll(Arrays.asList(new JSPAttribute("type","submit"), new JSPAttribute("value", "Upload") ));
			formEl.children.addAll(Arrays.asList(actAttr, methAttr, enctypeAttr, fileInputEl, inputEl));
			owner.children.add(formEl);
		} else if (helper_hasStereoType(e, UWEStereotypeClassPres.SELECTION)) {

		}
	}

	/// helper class
	public static boolean helper_hasStereoType(NamedElement c, UWEStereotypeClassPres stereoType){
		return StereotypesHelper.hasStereotype(c, stereoType.toString());
	}
	
	static ArrayList<String> relativeIndexClass = new ArrayList<String>();
	public static void helper_setupIndexClass(ArrayList<NamedElement> lstClass){
		for (NamedElement namedElement : lstClass) {
			relativeIndexClass.add(namedElement.getName());
		}
	}
	
	public static boolean helper_isRelativeToIndex(NamedElement e){
		return relativeIndexClass.contains(e.getName());
	}
	
	public static String helper_createFolderSaveJSP() {
		String path = System.getProperty("user.home") + File.separator + "Documents";
		path += File.separator + "UWEGenerationCode";
		File customDir = new File(path);
		if (!customDir.exists())
			if (!customDir.mkdirs())
				return "";
		String pathSave = path + File.separator + "JSP";
		customDir = new File(pathSave);
		if (!customDir.exists())
			if (!customDir.mkdirs())
				return "";
		return pathSave;
	}

	public static BufferedWriter helper_createJSPFile(String name) {
		String pathSave = helper_createFolderSaveJSP();
		if (pathSave.isEmpty())
			return null;
		BufferedWriter buffWriter = null;
		try {

			buffWriter = new BufferedWriter(new FileWriter(pathSave + File.separator + name, false));
			buffWriter.write("<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\"%> \n"
					+ "<%@taglib prefix=\"c\" uri=\"http://java.sun.com/jsp/jstl/core\"%> \n"
					+ "<%@taglib prefix=\"fn\" uri=\"http://java.sun.com/jsp/jstl/functions\" %> \n");
			buffWriter.flush();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return buffWriter;
	}

	public static void helper_write2File(JSPRoot r){
		BufferedWriter buffWriter = helper_createJSPFile(r.documentName);
		try {
			String beautifulContent = helper_makeBeatifulCode(r.toCode());
			buffWriter.write(beautifulContent);
			buffWriter.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static String helper_makeBeatifulCode(String content) {
		String beautifulContent = "";
		String str = "";
		int incTab = 0;
		BufferedReader reader = new BufferedReader(new StringReader(content));
		try {
			while ((str = reader.readLine()) != null) {
				if (str.substring(0, 2).compareTo("<>") == 0 || str.substring(0, 3).compareTo("</>") == 0) {
					continue;
				}
				if (str.substring(0, 2).compareTo("</") == 0) {
					incTab--;
					for (int i = 0; i < incTab; i++)
						beautifulContent += "\t";
					beautifulContent += str + "\n";
				} else if (str.substring(0, 1).compareTo("<") == 0) {
					for (int i = 0; i < incTab; i++)
						beautifulContent += "\t";
					beautifulContent += str + "\n";
					incTab++;
				} else {
					for (int i = 0; i < incTab; i++)
						beautifulContent += "\t";
					beautifulContent += str + "\n";
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return beautifulContent;
	}
}
