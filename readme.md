Assignable:
表示可有作为右值，包括CallInst,Const,LoadInst,BinaryInst

按照伪代码的思路比较难求，因此可以dfs，遍历到该节点便返回，这样所有只有该节点能遍历到的节点都不会被遍历

原方法：

```
private void calcDom(BasicBlock start) {
        while (update(start)) {
            for (BasicBlock basicBlock : start.getNext()) {
                calcDom(basicBlock);
            }
        }
    }
}
private boolean update(BasicBlock it) {
    int len = it.getDom().size();
    HashSet<BasicBlock> set = new HashSet<>();
    boolean first = true;
    for (BasicBlock basicBlock : it.getPrev()) {
        if (first) {
            first = false;
            set.addAll(basicBlock.getDom());
        } else {
            set.retainAll(basicBlock.getDom());
        }
    }
    set.add(it);
    it.setDom(set);
    return set.size() != len;
}



```

a+(b-a)
(a+b)+(a-b)

a-a
(a+b)-a
a-(a-b)

a0,k0,k1,v0拿来做临时寄存器
t0-t9 and s0-s7 v1 a1-a3用来做全局寄存器共22个

$0,$1,$ra,$fp,$gp,$sp不动

原本设计是调用函数时，把冲突的寄存器存起来，但是可能会存在，a和b不共用，b和c不共用，a和c共用的情况，导致寄存器没保存
但是盲目的全压栈，肯定是无意义的

出现了不分寄存器两个objerr,分的话一个objerr

原因临时寄存器出问题了，选定V1作为在翻译一个指令时可持久的寄存器

下一步优化：函数参数寄存器分配，以及确定函数调用时寄存器压栈情况

$a1-$a3分给函数参数

出现了$a1 <- 1
$a2 <- $a1 这样的错误
f(a,b,c)调用 f(b,c,a)
b <- a
c <- b
a <- c

实际效果会是 b,c,a <- old(a) 如果调整顺序，a<-c,c<-b,b<-a 就就合理