var alertbox;
var confirmbox;

YUI().use("panel", function (Y) {

	alertbox = new Y.Panel({
        contentBox : Y.Node.create('<div id="alertbox" />'),
        bodyContent: '<div class="message dialog-info">Are you sure you want to [take some action]?</div>',
        width      : 410,
        zIndex     : 6,
        centered   : true,
        modal      : true,
        render     : true,
        visible    : false, // make visible explicitly with .show()
        buttons    : {
            footer: [
                {
                    name     : 'proceed',
                    label    : 'OK',
                    action   : 'onOK'
                }
            ]
        }
    });

	alertbox.onOK = function (e) {
        e.preventDefault();
        this.hide();
        this.callback = false;
    } ;
    
	confirmbox = new Y.Panel({
        contentBox : Y.Node.create('<div id="confirmbox" />'),
        bodyContent: '<div class="message dialog-question">Are you sure you want to [take some action]?</div>',
        width      : 410,
        zIndex     : 6,
        centered   : true,
        render     : true,
        modal      : true,
        visible    : false, // make visible explicitly with .show()
        buttons    : {
            footer: [
                {
                    name  : 'cancel',
                    label : 'Cancel',
                    action: 'onCancel'
                },

                {
                    name     : 'proceed',
                    label    : 'OK',
                    action   : 'onOK'
                }
            ]
        }
    });

	confirmbox.onCancel = function (e) {
        e.preventDefault();
        this.hide();
        this.callback = false;
    } ;

	confirmbox.onOK = function (e) {
        e.preventDefault();
        this.hide();
        if(this.callback){
           this.callback();
        }
        this.callback = false;
    } ;

	confirmsidebox = new Y.Panel({
        contentBox : Y.Node.create('<div id="confirmsidebox" />'),
        bodyContent: '<div class="message dialog-question">Are you sure you want to [take some action]?</div>',
        width      : 410,
        zIndex     : 6,
        centered   : '#panelcenter',
        render     : true,
        modal      : true,
        visible    : false, // make visible explicitly with .show()
        buttons    : {
            footer: [
                {
                    name  : 'cancel',
                    label : 'Cancel',
                    action: 'onCancel'
                },

                {
                    name     : 'proceed',
                    label    : 'OK',
                    action   : 'onOK'
                }
            ]
        }
    });

	confirmsidebox.onCancel = function (e) {
        e.preventDefault();
        this.hide();
        this.callback = false;
    } ;

	confirmsidebox.onOK = function (e) {
        e.preventDefault();
        this.hide();
        if(this.callback){
           this.callback();
        }
        this.callback = false;
    } ;
    
});