package top.daheizi.commons.test;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import top.daheizi.commons.test.unit.Book;
import top.daheizi.commons.test.unit.Desk;
import top.daheizi.commons.test.unit.IBook;
import top.daheizi.commons.test.unit.OtherBook;
import top.daheizi.commons.test.unit.OtherDesk;
import top.daheizi.commons.test.unit.RoundDesk;
import top.daheizi.commons.test.util.ProxyHandler;
import top.daheizi.commons.test.util.TypeCounter;

import com.reign.util.random.weight.test.TestWeightChoosen.TestC1;

public class TestReflect {
    
    public static void main(String[] args){
//        OtherBook otherBook = new OtherBook();
//        Book book = (Book) otherBook;
//        Class<?> clazzA = book.getClass();
//        Class<?> clazzB = OtherBook.class;
//        Book anotherBook = Book.class.cast(otherBook);
//        System.out.println(anotherBook.getClass());
//        Class<?> clazzC = null;
//        try {
//            clazzC = Class.forName("com.hdaheizii.commons.test.unit.OtherBook");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        System.out.println(clazzA == clazzC);
//        System.out.println("Book.class.isAssignableFrom(clazzB) == " + 
//                Book.class.isAssignableFrom(clazzB));
//        
//        TypeCounter counter = new TypeCounter(Object.class);
//        counter.count(counter);
//        System.out.println(counter);
//        
//        Class<Integer> clazzD = int.class;
//        Class<Integer> clazzE = Integer.TYPE;
//        Class<Integer> clazzF = Integer.class;
//        System.out.println(clazzD.getName());
//        System.out.println(clazzE == clazzD);
//        System.out.println(clazzE == clazzF);
//        System.out.println(clazzD.isInstance(3));
//        
//        try {
//            Object obj = clazzB.newInstance();
//            Field field = clazzB.getDeclaredField("type");
//            Field field2 = clazzB.getDeclaredField("type");
//            System.out.println(field.equals(field2));
//            System.out.println(field == field2);
//            field.setAccessible(true);
//            field2.setAccessible(false);
//            System.out.println(field.get(obj));
//            field.set(obj, 70);
//            System.out.println(field.get(obj));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        int[] array = new int[]{3, 4};
//        Integer[] array2 = new Integer[]{0, 1};
//        System.out.println(array2.getClass().equals(array.getClass()));
//        @SuppressWarnings("unchecked")
//        Class<int[]> clazzG = (Class<int[]>) array.getClass();
//        Class<?> clazzH = array2.getClass();
//        System.out.println(clazzG.getName());
//        System.out.println(clazzH.getName());
//        
//        IBook proxyBook = (IBook) ProxyHandler.getDefaultProxyHandler(new OtherBook());
//        proxyBook.setNum(23, 3);
//        int num = proxyBook.getNum();
//        System.out.println(num);
//        
//        try {
//            Method method = clazzA.getMethod("doSomething", Integer.class, int[].class, String.class, int.class);
//            method.invoke(otherBook, 1, array,"xx", 2);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
//        try {
//            Method method = Book.class.getMethod("getNum");
//            int result = (int) method.invoke(new OtherBook());
//            System.out.println(method + " " + result);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        
        
//        try {
//            Method method = ReflectTest.class.getMethod("test", Book.class);
//            System.out.println(method);
//            method.invoke(null, new Book());
//            method.invoke(null, new OtherBook());
//            
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        
//        test2(new Book());
//        test2(new OtherBook());
//        test2(new Object());
        
//        List<? extends Integer> list = new ArrayList<>();
//        System.out.println(ArrayList.class == list.getClass());
        
//        System.out.println(new Desk<>(new Book()).f());
//        System.out.println(new OtherDesk<>(new OtherBook()).f());
        
        
//        Class<Desk> clazz = Desk.class;
//        try {
//            Constructor<Desk> con = clazz.getConstructor(Book.class);
//            Desk<?> desk = con.newInstance(new Book(17));
//            System.out.println(desk.f());
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        
//        Integer[][] array = new Integer[][]{new Integer[]{1,2}, new Integer[]{3,4}};
//        System.out.println(array.getClass().getComponentType().isArray());
//        System.out.println(Array.getLength(array));
//        System.out.println(Arrays.toString(array[0]));
//        System.out.println(Arrays.deepToString(array[1]));
//        System.out.println(Arrays.deepToString(array));
        
//        Class<?> clazzints = Array.newInstance(int.class, 0).getClass();
//        System.out.println(Object.class.isAssignableFrom(clazzints));
//        System.out.println(clazzints);    
//        System.out.println(Arrays.toString((int[])Array.newInstance(clazzints.getComponentType(), 4)));
        
        
        
        
        
//        Object obj = Array.newInstance(RoundDesk.class, 3);
//        Object obj2 = new OtherDesk<OtherBook>(new OtherBook());
//        
//        Class clazz = obj2.getClass();
//        
//        Type type = clazz.getTypeParameters()[0];
//        Type type2 = clazz.getGenericSuperclass();
        
//        if(type instanceof TypeVariable){
//            TypeVariable var = (TypeVariable) type;
//            System.out.println(var.getTypeName());
//            System.out.println(Arrays.toString(var.getBounds()));
//            System.out.println(var.getGenericDeclaration());
//        }
        
//        if(type2 instanceof ParameterizedType){
//            ParameterizedType generic = (ParameterizedType) type2;
//            System.out.println(generic.getTypeName());
//            System.out.println(Arrays.toString(generic.getActualTypeArguments()));
//            System.out.println(generic.getRawType());
//            System.out.println(generic.getOwnerType());
//            
//            
//            TypeVariable gvar = (TypeVariable) (generic.getActualTypeArguments()[0]);
//            System.out.println(gvar.getTypeName());
//            System.out.println(gvar.getGenericDeclaration());
//        }
        
        
    }
    
    public static void test0(List<? super Desk<? extends Book>> list){
        
    }
    
    
    public static void test(Book book){
        IBook proxy = (IBook) ProxyHandler.getDefaultProxyHandler(book);
        System.out.println("BOOK" + proxy.getNum());
    }
    
    public static void test(OtherBook book){
        IBook proxy = (IBook) ProxyHandler.getDefaultProxyHandler(book);
        System.out.println("OTHERBOOK" + proxy.getNum());
    }
    
    public static <T> void test2(T t){
        System.out.println("nothing");
    }

    public static <T extends Book> void test2(T t){
        System.out.println("extends");
    }
    
}
