import * as greycat from 'greycat';
import * as React from 'react';

export class Printer extends React.Component<String,String> {

    sayHello() {
        console.log("hello", greycat.Type.STRING, React);
    }

}