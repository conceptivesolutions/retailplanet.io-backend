package io.retailplanet.backend.common.objects.index;

import org.jetbrains.annotations.*;

import javax.json.bind.annotation.JsonbCreator;
import java.util.*;
import java.util.stream.*;

/**
 * Contains all necessary information about any match methods
 */
public class Match
{
  public String name;
  public String[] content;
  public String nestedPath;
  public List<Match> innerMatches;

  @JsonbCreator
  public Match()
  {
  }

  private Match(@NotNull String pName, @NotNull String... pContent)
  {
    name = pName;
    content = pContent;
  }

  /**
   * @return value of 'innerMatches' field
   */
  @Nullable
  public List<Match> innerMatches()
  {
    return innerMatches;
  }

  /**
   * @return value of 'name' field
   */
  @Nullable
  public String name()
  {
    return name;
  }

  /**
   * @return value of 'content' field
   */
  @Nullable
  public String[] content()
  {
    return content;
  }

  /**
   * @return value of 'nestedPath' field
   */
  @Nullable
  public String nestedPath()
  {
    return nestedPath;
  }

  /**
   * Mark this match as "nested document" match
   *
   * @param pPath specifies the nested path
   * @return Builder
   */
  @NotNull
  public Match nested(@NotNull String pPath)
  {
    nestedPath = pPath;
    return this;
  }

  /**
   * Creates an equals match to query elasticsearch by fieldname
   *
   * @param pFieldName  Name of the searched field
   * @param pFieldValue Searched value
   * @param pOperator   Operator to define how single words are handled. AND = all words must match, OR = One word must match
   * @return the match
   */
  @NotNull
  public static Match equal(@NotNull String pFieldName, @NotNull String pFieldValue, @NotNull Match.Operator pOperator)
  {
    return new Match("eq", pFieldName, pFieldValue, pOperator.name());
  }

  /**
   * Creates an or match query to define something like FIELD=XX or FIELD=YY
   *
   * @param pFieldName   Name of the field
   * @param pFieldValues Possible values
   * @return Match
   */
  @NotNull
  public static Match or(@NotNull String pFieldName, @NotNull List<String> pFieldValues)
  {
    Match match = new Match("or");
    List<String> values = new ArrayList<>(pFieldValues);
    values.add(0, pFieldName);
    match.content = values.toArray(new String[0]);
    return match;
  }

  /**
   * Combine matches to a single match
   *
   * @param pOperator Operator to define, how combinations are combined
   * @param pMatches  All matches
   * @return Builder
   */
  @NotNull
  public static Match combined(@NotNull Match.Operator pOperator, Match... pMatches)
  {
    Match match = new Match("combined", pOperator.name());
    match.innerMatches = Stream.of(pMatches)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    return match;
  }

  /**
   * Common "operator", to define contexts
   */
  public enum Operator
  {
    AND,
    OR
  }
}
