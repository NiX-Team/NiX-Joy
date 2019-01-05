# JS数组(改变原数组的方法)
## 1.push方法
* 描述:往数组末尾添加元素，可一次添加多个
* 代码:
```javaScript
    Array.prototype.push = function(){
        for(var i = 0; i < arguments.length; i++){
            this[this.length] = arguments[i];
        }
        return this.length;
    }
```
## 2.pop方法
* 描述:移除数组中的最后一个元素，参数没有效果
* 代码:
```javaScript
    Array.prototype.pop = function(){
        var temp = this[this.length - 1];
        this.length = this.length - 1;
        return temp;
    }
```
## 3.shift方法
* 描述:移除数组最开始的元素，只能一次移除一个，参数没有效果
* 代码:
```javaScript
    Array.prototype.shift = function(){
        var temp = this[0];
        for(var i = 0; i < this.length - 1; i++){
            this[i] = this[i + 1];
        }
        this.length--;
        return temp;
    }
```
## 4.unshift方法
* 描述:往数组头部添加元素，可一次添加多个
* 代码:
```javaScript
    Array.prototype.unshift = function(){
        var oldLength = this.length;
        var temp = this.length;
        var newLength = arguments.length + this.length;
        for(var i = 0; i < temp; i ++){
            this[--newLength] = this[--oldLength];
        }
        for(var i = 0; i < arguments.length; i ++){
            this[i] = arguments[i];
        }
        return this.length;
    }
```
## 5.sort方法
* 描述:用来排序数组，可自定义<br/>
&emsp; &emsp;排序过程：1.将数组中的第一个位置的元素与第二个元素位置比较<br/>
&emsp; &emsp;如果第一个元素大于第二个，则将两个元素换位<br/>
&emsp; &emsp;如果第一个元素小于第二个，则元素的位置不变<br/>
&emsp; &emsp;此时再将第一个位置的元素与第三个位置的元素比较<br/>
&emsp; &emsp;一次遍历以后，第二个位置的元素与第三个位置的元素比较<br/>
&emsp; &emsp;规则如上直至第n-1个元素与第n个元素比较完为止，整个过程类似于选择排序<br/>
* 自定义sort方法如下代码:
```javaScript
    var arr = [5,6,1,9,0,3];
    function newSort(x, y){
        //顺序
        //return x - y;
        //逆序
        return y - x;
    }
    //使用自定义sort()
    arr.sort(newSort);
    //特殊的方法，打乱原数组，每次调用返回的结果都不一样
    function mixtureArr(){
        return 0.5 - Math.random();
    }
```
## 6.reverse方法
* 描述:将数组倒置,即按原数组排列顺序的相反顺序排列
* 代码:
```javaScript
    Array.prototype.reverse = function(){
        var temp;
        for(var i = 0; i < this.length / 2; i ++){
            temp = this[i];
            this[i] = this[this.length-i-1];
            this[this.length-i-1] = temp;
        }
        return this;
    }
```
## 7.splice方法
* 描述:用来剪切数组，参数可分三种，一个参数，两个参数以及多于两个参数<br/>
&emsp; &emsp;一个参数或两个参数时候的第一个参数代表切的位置，可为负数，真正的位置为该负数加上数组长度代表的位置<br/>
&emsp; &emsp;如果代表真正位置的数还是为负数，则从第一个元素开始<br/>
&emsp; &emsp;如果第一个参数的值大于数组的长度或者第二个参数的值为负数，则相当于没有切，返回空数组<br/>
&emsp; &emsp;一个参数的时候，从该参数代表的位置开始切，直到数组最后<br/>
&emsp; &emsp;两个参数的时候，第一个参数代表切的开始位置，第二个参数代表要切的个数<br/>
&emsp; &emsp;多个参数的时候，第二个参数往后的所有参数，均为从切口位置要插入的元素<br/>
&emsp; &emsp;该方法返回切下的数组，原数组为剩下的部分<br/>
* 代码:
```javaScript
    Array.prototype.splice = function(){
        var splicePart = [];
        if(arguments[0] >= this.length || arguments[1] < 0){
            return splicePart;
        }
        var index = (arguments[0] >= 0 && arguments[0] < this.length) ? arguments[0] : (arguments[0] + this.length < 0 ? 0 : arguments[0] + this.length);
        var border = index + arguments[1] <= this.length ? arguments[1] : this.length - index;
        var current = 0;
        for(var i = index; i < index + border; i ++){
            splicePart[current++] = this[i];
        }
        if (arguments.length <= 2) {
            for(var i = index; i < this.length; i++){
                this[i] = this[i + border];
            }
            this.length = this.length - border;
            return splicePart;
        }else{
            var argu_length = arguments.length - 2;
            var new_length = this.length + argu_length - border;
            for(var i = 0; i < this.length - index - border; i++){
                this[i + index + argu_length] = this[i + index + border];
            }
            for(var i = 0; i < argu_length; i++){
                this[i + index] = arguments[i + 2];
            }
            this.length = this.length - border + argu_length;
            return splicePart;
        }
    }
```







## 贡献人员名单

名单按照字母顺序排序。

* [zhangzhimiao](https://github.com/zhangzhimiao)

## CHANGELOG

* v1.0 2018/12/09 无图版