import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class SimpleMapper {

    /**
     * 将对象转换为字符串
     * @param obj 待序列化对象
     * @return 对象序列化后的字符串
     */
    public static String toString(Object obj){
        try {
            Class<?> cls = obj.getClass();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(cls.getName()+"\n");

            for (Field field:cls.getDeclaredFields()){
                if (!field.isAccessible()){
                    field.setAccessible(true);
                }
                stringBuilder.append(field.getName()+"="+field.get(obj).toString()+"\n");
            }
            return stringBuilder.toString();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void setFieldValue(Field field,Object object,String value) throws Exception {
        Class<?> type = field.getType();
        if (type == int.class){
            field.setInt(object,Integer.parseInt(value));
        }else if (type == byte.class) {
            field.setByte(object, Byte.parseByte(value));
        } else if (type == short.class) {
            field.setShort(object, Short.parseShort(value));
        } else if (type == long.class) {
            field.setLong(object, Long.parseLong(value));
        } else if (type == float.class) {
            field.setFloat(object, Float.parseFloat(value));
        } else if (type == double.class) {
            field.setDouble(object, Double.parseDouble(value));
        } else if (type == char.class) {
            field.setChar(object, value.charAt(0));
        } else if (type == boolean.class) {
            field.setBoolean(object, Boolean.parseBoolean(value));
        } else if (type == String.class) {
            field.set(object, value);
        }else {
            //对于基本类型和String以外的类型，假定该类型有一个以String类型为参数的构造方法
            Constructor<?> constructor = type.getConstructor(new Class[]{String.class});
            field.set(object,constructor.newInstance(value));
        }

    }

    public static Object fromString(String str) {
        try {
            String[] lines = str.split("\n");
            if (lines.length < 1){
                throw new IllegalArgumentException(str);
            }
            Class<?> cls = Class.forName(lines[0]);
            Object obj = cls.newInstance();
            if (lines.length>1){
                for (int i = 1;i<lines.length;i++){
                    String[] fv = lines[i].split("=");
                    if (fv.length!=2){
                        throw new IllegalArgumentException(lines[i]);
                    }
                    Field field = cls.getDeclaredField(fv[0]);
                    if (!field.isAccessible()){
                        field.setAccessible(true);
                    }
                    setFieldValue(field,obj,fv[1]);
                }
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
