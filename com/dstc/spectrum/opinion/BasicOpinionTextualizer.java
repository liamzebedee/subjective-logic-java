package com.dstc.spectrum.opinion;

public class BasicOpinionTextualizer
  implements OpinionTextualizer
{
  public String textualize(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null.");
    }
    return opinion.toString();
  }
}