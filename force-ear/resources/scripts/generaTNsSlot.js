document.properties.content.guessMimetype(document.properties.name);
if(document.properties.content.mimetype.indexOf('pdf')> -1 ||
document.properties.content.mimetype.indexOf('image')> -1 ||
document.properties.content.mimetype.indexOf('text')> -1 ||
document.properties.content.mimetype.indexOf('rtf')> -1 ||
document.properties.content.mimetype.indexOf('word')> -1 ||
document.properties.content.mimetype.indexOf('powerpoint')> -1 ||
document.properties.content.mimetype.indexOf('opendocument.spreadsheet')> -1 ||
document.properties.content.mimetype.indexOf('excel')> -1 ){
renditionService.render(document,"cm:doclib");
renditionService.render(document,"cm:imgpreview");
}