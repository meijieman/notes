## 类中定义接口 vs 接口中定义类
```java
class A {
   interface B {}
}

interface D {
   class E {}
} 
```




```java
public interface Printable {
    void print();

    public static class Caller {
        public static void print(Object mightBePrintable) {
                if (mightBePrintable instanceof Printable) {
                        ((Printable) mightBePrintable).print();
                }
        }
    }
}
```

```java
void genericPrintMethod(Object obj) {
    if (obj instanceof Printable) {
        ((Printable) obj).print();
    }
}
```
可以写为
```java
void genericPrintMethod(Object obj) {
	 Printable.Caller.print(obj);
}
```

那么，各有什么优点呢？


