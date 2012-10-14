package com.dstc.spectrum.opinion;

import java.util.EventObject;

public class OpinionEvent extends EventObject
{
  private static final long serialVersionUID = 1L;

  public OpinionEvent(Opinion source)
  {
    super(source);
  }

  public Opinion getOpinion()
  {
    return (Opinion)this.source;
  }
}