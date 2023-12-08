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