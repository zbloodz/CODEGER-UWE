package magicUWE.transformation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Generalization;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

public class Proc2JavaTransformation {
	public static void Process2ControllerTransformation(ArrayList<NamedElement> listClass) {		
		String content = "";
		for (NamedElement namedElement : listClass) {
			Class c = (Class) namedElement;
			JavaClass javaClass = UWEClass2JavaClass(c);
			Cont2JavaTransformation.UWEProperty2JavaClassMembers(javaClass, c);
			Cont2JavaTransformation.UWEOperation2JavaMethod(javaClass, c);
			content += javaClass.toCode();
		}
			
		BufferedWriter buffWriter = helper_createProcessFile("Process.java", "uwe.transformation.controller");
		//write file
		try {
			buffWriter.write(content);
			buffWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static JavaClass UWEClass2JavaClass(Class c){
		JavaClass javaClass = new JavaClass();
		javaClass.name = c.getName();
		javaClass.isAbstract = c.isAbstract();
		javaClass.isInterface = false;
		javaClass.isPublic = false;
		
		//add super class
		if (c.hasSuperClass()) {		
			Iterator<Generalization> itGens = c.getGeneralization().iterator();
			while (itGens.hasNext()) {
				Generalization g = itGens.next();
				JavaClass superClass = new JavaClass();
				superClass.isInterface = false;
				superClass.name = g.getTarget().iterator().next().getHumanName().substring(6);
				javaClass.superClasses.add(superClass);			
				}
		}
		return javaClass;
	}
	
	//helper
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

	public static BufferedWriter helper_createProcessFile(String fileName, String packageName) {
		String pathSave = helper_createFolderSaveController();
		if (pathSave.isEmpty())
			return null;
		BufferedWriter buffWriter = null;
		try{
			buffWriter = new BufferedWriter(new FileWriter(pathSave.toString() + File.separator + fileName, false));
			buffWriter.write( "package " + packageName + ";\n\n"
							+ "import java.util.*" + ";\n\n");			
			buffWriter.flush();
		}catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return buffWriter;
	}
}