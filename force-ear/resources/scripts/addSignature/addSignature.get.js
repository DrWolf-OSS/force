var document = search.findNode(args.target);

model.signature = null;

//document.addAspect('dw:signed');
if(document!=null && document.hasAspect('dw:signed')){
  model.signature = document.createNode(args.name,'dw:signature','dw:signatures');
}

