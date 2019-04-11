package magicUWE.transformation;

import java.util.*;
import java.io.*;

//
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Generalization;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralInteger;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralUnlimitedNatural;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.MultiplicityElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Parameter;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.PrimitiveType;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Operation;

abstract class JavaElement
{
	public String name = "";
	public String toCode(){
		return name;
	}
}

class JavaPackage extends JavaElement{
	@Override
	public String toCode(){
		if(this.name.isEmpty())
			return "";
		return "package " + this.name + ";\n\n";
	}
}

abstract class JavaClassMember extends JavaElement{
	public boolean isStatic;
	public boolean isPublic;
	public JavaType type = new JavaType();
	public String visibility(){
		if(this.isPublic) 
			return "public ";
		return "private ";
	}
	public String scope(){
		if(this.isStatic)
			return "static ";
		return "";
	}
}

class JavaField extends JavaClassMember{
	public String initialize = "";
	@Override
	public String toCode(){
		String content = "";
		content += "\t" + this.visibility() + this.scope() + this.type.fullName() + " " + this.name;
		if(!this.initialize.isEmpty())
			content += " = " + this.initialize;
		content += ";\n";
		return content;
	}
}

class JavaType extends JavaElement{
	public String fullName(){
		return this.name;
	}
}

class JavaClass extends JavaType{
	public boolean isAbstract = false;
	public boolean isPublic = false;
	public boolean isInterface = false;
	public String anotation = "";
	
	public JavaPackage javaPackage = new JavaPackage();
	public List<JavaClass> superClasses = new ArrayList<JavaClass>();
	public List<JavaClassMember> members = new ArrayList<JavaClassMember>();
	public List<JavaClass> actualTypeParameters = new ArrayList<JavaClass>();

	public String visibility(){
		if(this.isPublic) 
			return "public ";
		return "";
	}
	public String modifierAbstract(){
		if(this.isAbstract)
			return "abstract ";
		return "";
	}
	
	@Override
	public String fullName(){
		String content = "";
		if(this.javaPackage.name != "")
			content += this.javaPackage.name + ".";
		content += this.name;
		if(!this.actualTypeParameters.isEmpty()){
			content += "<";
			String acc = "";
			for (JavaClass tp : actualTypeParameters) {
				if(!acc.isEmpty())
					acc += ",";
				acc += tp.fullName();
			}
			content += ">";
		}
		return content;
	}
	@Override
	public String toCode(){
		String content = "";
		content += anotation + "\n";
		content += this.visibility() + this.modifierAbstract();
		if(this.isInterface)
			content += "interface ";
		else
			content += "class ";
		content += this.name;
		//superclass
		if(!this.superClasses.isEmpty()){
			String acc = "";
			for (JavaClass sc : this.superClasses) {
				if(!sc.isInterface || this.isInterface)
				{
					if(acc.isEmpty())
						acc += " extends ";
					else
						acc += ", ";
					acc += sc.fullName();
				}
			}
			content += acc;
			acc = "";
			for (JavaClass sc : this.superClasses) {
				if(sc.isInterface && !this.isInterface)
				{
					if(acc.isEmpty())
						acc += " implements ";
					else
						acc += ", ";
					acc += sc.fullName();
				}
			}
			content += acc;
		}
		//code member
		content += "{\n";
		String acc = "";
		for (JavaClassMember i : this.members) {
			acc += i.toCode();
		}
		content += acc + "\n}\n\n";
		return content;
	}
}

class JavaMethod extends JavaClassMember{
	public String body = "";
	public String anotation = "";
	public List<JavaMethodParameter> parameters = new ArrayList<JavaMethodParameter>();
	public List<JavaClass> exceptions = new ArrayList<JavaClass>();
	@Override
	public String toCode(){
		String content = "";
		if(!anotation.isEmpty())
			content += "\t" + anotation + "\n";
		content += "\t" + this.visibility() + this.scope();
		content += (this.type.name.isEmpty())?"void":this.type.fullName();
		content += " " + this.name + "(";
		if(!this.parameters.isEmpty()){
			String acc = "";
			for (JavaMethodParameter i : this.parameters) {
				if(!acc.isEmpty())
					acc += ",";
				acc += i.toCode();
			}
			content += acc;
		}
		content += ")";
		if(!this.exceptions.isEmpty()){
			String acc = "";
			for(JavaClass i : this.exceptions){
				if(!acc.isEmpty()){
					acc += ",";
				}
				acc += i.fullName();
			}
			content += " throws " + acc;
		}
		content += "{\n\t\t" + this.body + "\n\t}\n";
		return content;
	}
}

class JavaMethodParameter extends JavaElement{
	public JavaType type = new JavaType();
	@Override
	public String toCode(){
		return this.type.fullName() + " " + this.name;
	}
}

class Enumeration extends JavaType{
	public JavaPackage javaPackage;
	public List<EnumerationLiteral> enumerationLiterals = new ArrayList<EnumerationLiteral>();
	public String fullName(){
		if(!this.javaPackage.name.isEmpty())
			return this.javaPackage.name + "." + this.name;
		return this.name;
	}
	@Override
	public String toCode(){
		String content = "";
		content += this.javaPackage.toCode();
		content += "public enum  " + this.name + "{\n\t";
		String acc = "";
		for (EnumerationLiteral el : enumerationLiterals) {
			if(!acc.isEmpty())
				acc += ",";
			acc += el.name;
		}
		content += acc + ";\n}\n\n";
		return content;
	}
}

class EnumerationLiteral extends JavaElement{
	
}

public class Cont2JavaTransformation {
	public static void Content2EntityTransformation(Class c) {
		JavaClass javaClass = UWEClass2JavaClass(c);
		UWEProperty2JavaClassMembers(javaClass, c);
		UWEOperation2JavaMethod(javaClass, c);
		
		BufferedWriter buffWriter = helper_createEntityFile(c.getName() + ".java", javaClass.javaPackage.toCode());
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
	
	public static JavaClass UWEClass2JavaClass(Class c){
		JavaClass javaClass = new JavaClass();
		javaClass.name = c.getName();
		javaClass.javaPackage.name = uwe_getPackageName(c);
		javaClass.isAbstract = c.isAbstract();
		javaClass.isInterface = false;
		javaClass.isPublic = true;
		
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
		JavaMethod method = new JavaMethod();
		method.isPublic = true;
		method.isStatic = false;
		method.name = c.getName();
		method.type.name = " ";
		method.body = "";
		
		javaClass.members.add(method);
		return javaClass;
	}
	
	public static void UWEProperty2JavaClassMembers(JavaClass owner, Class c){
		Iterator<Property> itProps = c.getAttribute().iterator();
		while (itProps.hasNext()) {
			Property prop = itProps.next();
			if (!prop.getName().isEmpty()) {				
				if (!prop.isDerived()) {					
					// field
					JavaField javaField = new JavaField();
					javaField.name = "_" + prop.getName();
					javaField.type.name = uwe_getType(c, prop);
					javaField.isPublic = false;
					javaField.isStatic = false;
					javaField.initialize = uwe_getInitialize(c, prop);
					owner.members.add(javaField);
					
					//getter
					JavaMethod getter = new JavaMethod();
					getter.isPublic = true;
					getter.isStatic = false;
					getter.name = "get" + prop.getName().substring(0, 1).toUpperCase() + prop.getName().substring(1);
					getter.type.name = uwe_getType(c, prop);
					getter.body = "return " + "_" + prop.getName() + ";";
					owner.members.add(getter);
					
					//setter
					JavaMethod setter = new JavaMethod();
					setter.isPublic = true;
					setter.isStatic = false;
					setter.name = "set" + prop.getName().substring(0, 1).toUpperCase() + prop.getName().substring(1);
					setter.body = "this." + "_" + prop.getName() + "=" + "_" + prop.getName() + ";";
					JavaMethodParameter setterPara = new JavaMethodParameter();
					setterPara.name = "_" + prop.getName();
					setterPara.type.name = uwe_getType(c, prop);
					setter.parameters.add(setterPara);
					owner.members.add(setter);
				} 
				else {
					//derived property
					//getter
					JavaMethod getter = new JavaMethod();
					getter.isPublic = true;
					getter.isStatic = false;
					getter.type.name = uwe_getType(c, prop);
					getter.name = "get" + prop.getName().substring(0, 1).toUpperCase() + prop.getName().substring(1);
					getter.body = uwe_getBodyFromType(prop.getType());
					owner.members.add(getter);
				}
			}
		}		
	}
	
	public static void UWEOperation2JavaMethod(JavaClass owner,Class c){
		Iterator<Operation> itOps = c.getOwnedOperation().iterator();
		while (itOps.hasNext()) {
			Operation op = itOps.next();
			if (!op.getName().isEmpty()) {		
				JavaMethod method = new JavaMethod();
				method.isPublic = true;
				method.isStatic = false;
				method.name = op.getName();
				method.type.name = uwe_getTypeName(c, op.getType());
				method.body = uwe_getBodyFromType(op.getType());
				Iterator<Parameter> itParams = op.getOwnedParameter().iterator();
 				while (itParams.hasNext()) {
 					Parameter param = itParams.next();
 					JavaMethodParameter methodParam = new JavaMethodParameter();
 					methodParam.name = param.getName();
 					methodParam.type.name = uwe_getTypeName(c, param.getType());
 					method.parameters.add(methodParam);
 				}
 				owner.members.add(method);
			}
		}
	}
	
	//helper
	public static String helper_createFolderSaveEntities() {
		String path = System.getProperty("user.home") + File.separator + "Documents";
		path += File.separator + "UWEGenerationCode";
		File customDir = new File(path);
		if (!customDir.exists())
			if (!customDir.mkdirs())
				return "";
		String pathSave = path + File.separator + "Entities";
		customDir = new File(pathSave);
		if (!customDir.exists())
			if (!customDir.mkdirs())
				return "";
		return pathSave;
	}

	public static BufferedWriter helper_createEntityFile(String fileName, String packageName) {
		String pathSave = helper_createFolderSaveEntities();
		if (pathSave.isEmpty())
			return null;
		BufferedWriter buffWriter = null;
		try{
			buffWriter = new BufferedWriter(new FileWriter(pathSave.toString() + File.separator + fileName, false));
			buffWriter.write(packageName + "\n" 
						+ "import java.util.*;" + "\n\n");			
			buffWriter.flush();
		}catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return buffWriter;
	}

	public static boolean uwe_isMultivalued(Property p){
		if(((MultiplicityElement)p).getUpperValue() == null || ((MultiplicityElement)p).getLowerValue() == null)
			return false;
		Integer upperValue = 0;
		if (((MultiplicityElement) p).getUpperValue() != null) {
			if (((MultiplicityElement) p).getUpperValue() instanceof LiteralInteger) {
				upperValue = ((LiteralInteger) ((MultiplicityElement) p).getUpperValue()).getValue();
			} else if (((MultiplicityElement) p).getUpperValue() instanceof LiteralUnlimitedNatural) {
				upperValue = (Integer) ((LiteralUnlimitedNatural) ((MultiplicityElement) p).getUpperValue()).getValue();
			}
		}
		if(upperValue == -1 || upperValue > 1)
			return true;
		else return false;
	}
	
	public static String uwe_getType(Class owner, Property p){
		String res = "";
		if(uwe_isMultivalued(p)){
			if(p.isOrdered()){
				res += "List";
			}
			else{
				res += "Set";
			}
			res += "<" + uwe_getTypeName(owner, p.getType()) + ">";
		}
		else{
			res += uwe_getTypeName(owner,p.getType());
		}	
		return res;
	}

	public static String uwe_getTypeName(Class owner, Type t){
		if(t == null)
			return "void";
		if(t instanceof PrimitiveType){		
			if(t.getName().equals("String"))
				return "String";
			else if(t.getName().equals("Integer"))
				return "Integer";
			else if(t.getName().equals("Boolean"))
				return "boolean";
			else
				return "void";
		}
		else
			return uwe_getPackageName(owner) + "." + t.getName(); 
	}
	
	public static String uwe_getInitialize(Class owner, Property p){
		String res = "";
		if(p.getType().getName().equals("String"))
			return res;
		else if(uwe_isMultivalued(p)){
			if(p.isOrdered()){
				res += "=" + "new ArrayList<" + uwe_getTypeName(owner, p.getType()) + ">()";
			}
			else{
				res += "=" + "new HashSet<" + uwe_getTypeName(owner, p.getType()) + ">()";
			}
		}
		return res;
	}
	
	public static String uwe_getBodyFromType(Type p){
		if(p == null)
			return "return;";
		if(p.getName().equals("void"))
			return "return;";
		else if(p.getName().equals("Boolean"))
			return "return false;";
		else
			return "return null;";
	}

	public static String uwe_getPackageName(Class c){
		return "uwe.transformation.beans";
	}
}

