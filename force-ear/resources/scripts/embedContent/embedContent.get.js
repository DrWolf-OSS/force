var document = search.findNode(args.target);

if(document.hasAspect('dw:signed')){
        var content = search.findNode(args.content);
	var empty = document.createNode(args.name,'cm:content','dw:content');
	empty.properties.content = content.properties.content;
	empty.mimetype = content.mimetype;
	empty.save();

	if(empty.mimetype.indexOf('pdf')> -1 ||
	empty.mimetype.indexOf('image')> -1 ||
	empty.mimetype.indexOf('text')> -1 ||
	empty.mimetype.indexOf('rtf')> -1 ||
	empty.mimetype.indexOf('word')> -1 ||
	empty.mimetype.indexOf('powerpoint')> -1 ||
	empty.mimetype.indexOf('opendocument.spreadsheet')> -1 ||
	empty.mimetype.indexOf('excel')> -1 ){
		renditionService.render(empty,"cm:doclib");
		renditionService.render(empty,"cm:imgpreview");
	}
}
model.result = 'embedded';


