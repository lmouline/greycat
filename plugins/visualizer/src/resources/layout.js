/// <reference path="jquery.d.ts" />
var configLayout = {
    content: [{
            type: 'row',
            content: [{
                    type: 'column',
                    content: [{
                            type: 'component',
                            componentName: 'testComponent',
                            componentState: { label: 'A' }
                        }, {
                            type: 'component',
                            componentName: 'testComponent',
                            componentState: { label: 'B' }
                        }]
                },
                {
                    type: 'component',
                    componentName: 'testComponent',
                    componentState: { label: 'C' }
                }]
        }]
};
var savedState = localStorage.getItem('savedState');
var layout;
if (savedState != null) {
    layout = new window.GoldenLayout(JSON.parse(savedState));
}
else {
    layout = new window.GoldenLayout(configLayout);
}
layout.registerComponent('testComponent', function (container, componantState) {
    container.getElement().html('<h1>' + componantState.label + '</h1>');
});
layout.on('tabCreated', function (tab) {
    tab
        .closeElement.remove();
});
layout.on('stackCreated', function (stack) {
    //remove close icon
    stack
        .header
        .controlsContainer
        .find('.lm_close')
        .remove();
});
layout.on('stateChanged', function () {
    var state = JSON.stringify(layout.toConfig());
    localStorage.setItem('savedState', state);
});
layout.init();
