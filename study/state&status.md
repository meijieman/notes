### state
1. state倾向于condition，是一种延续性的状态。status常用于描述一个过程中的某阶段（phase），类似于C语言中枚举型变量某一个固定的值，这个值属于一个已知的集合。 
2. state所指的状态，一般都是有限的、可列举的

### status
status则是不可确定的。

比如
```
    readyState -- 就那么四五种值  
    statusText -- 描述性的文字，可以任意  
    onreadystatechange -- 那么四五种值之间发生变化  
    window.status -- 描述性的文字，可以任意
```
