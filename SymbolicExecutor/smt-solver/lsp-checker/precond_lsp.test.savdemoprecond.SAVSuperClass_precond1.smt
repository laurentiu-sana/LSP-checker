(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrasorts ( Ref )
  :extrafuns ( (assertion_disabled Int) )
  :extrafuns (  ( x Int )  )
  :extrafuns (  ( input.member Int )  )
  :extrafuns (  )
  :extrafuns (  )
  :formula
    (or  (not (implies (not (and  ( < input.member 5 ))) (not (and  ( < input.member 10 )))) ) )
)
