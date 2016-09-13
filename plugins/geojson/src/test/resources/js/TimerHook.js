/**
 * Created by gnain on 12/09/16.
 */

var TimerHookFactory = function() {

    function HookTimer() {
        return {
            stack: [],
            history: [],
            level: 0,

            start: function (context) {
                console.log("Start", context);
                var record = {context: context, start: (new Date()).getTime(), activity: []}
                this.stack.push(record);
                this.history.push(record);
            },
            beforeAction: function (action, context) {
                var record = {context: context, action: action, start: (new Date()).getTime()};
                this.stack.push(record);

                var activityArray = this.history[this.history.length - 1]["activity"];
                for (var i = 0; i < this.level; i++) {
                    activityArray = activityArray[activityArray.length - 1]["activity"];
                }
                activityArray.push(record);
            },
            afterAction: function (action, context) {
                var record = this.stack.pop();
                record["end"] = (new Date()).getTime();
                record["duration"] = (record["end"] - record["start"]) + "ms";
                //console.log("Action:" + action.toString(), record);
            },
            beforeTask: function (parentContext, context) {
                var record = {
                    context: context,
                    parentContext: parentContext,
                    start: (new Date()).getTime(),
                    activity: []
                };
                this.stack.push(record);

                var activityArray = this.history[this.history.length - 1]["activity"];
                for (var i = 0; i < this.level; i++) {
                    activityArray = activityArray[activityArray.length - 1]["activity"];
                }
                activityArray.push(record);
                this.level++;
                //console.log("Before task", parentContext, context);
                //this.timers[parentContext][context] = {startTask:(new Date()).getTime()};
            },
            afterTask: function (context) {
                var record = this.stack.pop();
                record["end"] = (new Date()).getTime();
                record["duration"] = (record["end"] - record["start"]) + "ms";
                //console.log("After task", record);
                this.level--;
                //this.timers[context]["endTask"] = (new Date()).getTime();
            },
            end: function (context) {
                var record = this.stack.pop();
                record["end"] = (new Date()).getTime();
                record["duration"] = (record["end"] - record["start"]) + "ms";
                console.log("End", record);
                //console.log("History", this.history);
            }
        }
    };

    return {
        newHook: function () {
            return new HookTimer();
        }
    };

};

