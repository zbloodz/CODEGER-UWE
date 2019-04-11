package magicUWE.transformation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;

import magicUWE.stereotypes.UWEStereotypeClassNav;

class NaviNode{
	public NamedElement e;
	public String linkNode = "";
	public NaviNode(){
	}
	public NaviNode(NamedElement e, String linkNode){
		this.e = e;
		this.linkNode = linkNode;		
	}
}
public class Nav2JavaTransformation {
		
	public static void Navigation2ControllerTransformation(NamedElement r) {		
		JavaClass javaClass = CreateClassController(r);
		ArrayList<NaviNode> naviNode = CreateListNaviNode(r);
		for (NaviNode node : naviNode) {
			NaviNode2JavaMethod(javaClass, node);
		}		
		BufferedWriter buffWriter = helper_createControllerFile(javaClass.name + ".java", "uwe.transformation.controller");
		String content = javaClass.toCode();	
		//write file
		try {
			buffWriter.write(content);
			buffWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static ArrayList<NaviNode> CreateListNaviNode(NamedElement r){
		ArrayList<NaviNode> naviNode = new ArrayList<NaviNode>();
		Map<String, Integer> visit = new HashMap<String, Integer>();
		visit.put(r.getQualifiedName(), 0);
		ArrayList<NaviNode> queue = new ArrayList<NaviNode>();
		queue.add(new NaviNode(r, ""));		
		while(!queue.isEmpty()){
			NaviNode s = queue.get(0);
			queue.remove(0);
			if(StereotypesHelper.hasStereotype(s.e, "processClass") || StereotypesHelper.hasStereotype(s.e, UWEStereotypeClassNav.QUERY.toString())){
				naviNode.add(s);			
			}			
			ArrayList<NamedElement> lstElements = helper_getAssElement(s.e);
			for (NamedElement ne : lstElements) {
				if (visit.get(ne.getQualifiedName()) == null || visit.get(ne.getQualifiedName()) == 0) {
					visit.put(ne.getQualifiedName(), 1);
					queue.add(new NaviNode(ne, "/" + ne.getName()));
				}
			}
		}
		return naviNode;
	}
	
	public static JavaClass CreateClassController(NamedElement r){
		JavaClass javaClass = new JavaClass();
		javaClass.anotation ="@Controller \n@RequestMapping(\"/" + r.getName() + "\")";
		javaClass.name = r.getName() + "Controller";
		javaClass.isAbstract = false;
		javaClass.isInterface = false;
		javaClass.isPublic = true;
		
		//add contruction
		JavaMethod method = new JavaMethod();
		method.isPublic = true;
		method.isStatic = false;
		method.anotation = "@RequestMapping(method=RequestMethod.GET)";
		method.name = "home";
		method.type.name = "String";
		method.body = "return \"" + r.getName() + "\";";
		javaClass.members.add(method);
		
		return javaClass;
	}
	
	public static void NaviNode2JavaMethod(JavaClass owner, NaviNode node){		
		JavaMethod method = new JavaMethod();
		method.isPublic = true;
		method.isStatic = false;
		method.anotation = "@RequestMapping(value = \"" + node.linkNode + "\")";
		method.name = node.e.getName();
		method.type.name = "String";
		method.body = "return \"\";";

		JavaMethodParameter methodParam = new JavaMethodParameter();
		methodParam.name = "model";
		methodParam.type.name = "ModelMap";
		method.parameters.add(methodParam);

		owner.members.add(method);
	}
	
	// helper
	public static String helper_createFolderSaveController() {
		String path = System.getProperty("user.home") + File.separator + "Documents";
		path += File.separator + "UWEGenerationCode";
		File customDir = new File(path);
		if (!customDir.exists())
			if (!customDir.mkdirs())
				return "";
		String pathSave = path + File.separator + "Controller";
		customDir = new File(pathSave);
		if (!customDir.exists())
			if (!customDir.mkdirs())
				return "";
		return pathSave;
	}

	public static BufferedWriter helper_createControllerFile(String fileName, String packageName) {
		String pathSave = helper_createFolderSaveController();
		if (pathSave.isEmpty())
			return null;
		BufferedWriter buffWriter = null;
		try{
			buffWriter = new BufferedWriter(new FileWriter(pathSave.toString() + File.separator + fileName, false));
			buffWriter.write("package " + packageName + ";\n\n"
					+ "import java.util.*;" + "\n\n"
					+ "import org.springframework.stereotype.Controller;\n"
					+ "import org.springframework.web.bind.annotation.PathVariable;\n"
					+ "import org.springframework.web.bind.annotation.RequestMapping;\n"
					+ "import org.springframework.web.bind.annotation.RequestMethod;\n"
					+ "import org.springframework.ui.ModelMap;\n\n");
			buffWriter.flush();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return buffWriter;
	}
	
	public static ArrayList<NamedElement> helper_getAssElement(NamedElement e){
		ArrayList<NamedElement> lstElement = new ArrayList<NamedElement>();
		Iterator<Property> itProps = ((Class) e).getAttribute().iterator();
		while (itProps.hasNext()) {
			Association ass = itProps.next().getAssociation();
			if (ass != null) {
				Iterator<Element> itElements = ass.getRelatedElement().iterator();
				NamedElement n1 = (NamedElement) itElements.next();
				NamedElement n2 = (NamedElement) itElements.next();
				if (n1.getQualifiedName().compareTo(e.getQualifiedName()) == 0) {
					lstElement.add(n2);
				} else {
					lstElement.add(n1);
				}
			}
		}
		return lstElement;
	}
}
