(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrasorts ( Ref )
  :extrafuns ( (assertion_disabled Int) )
  :extrafuns (  )
  :extrafuns (  ( input.sum Int )  ( input.member Int )  )
  :extrafuns (  )
  :extrafuns (  )
  :formula
    (or  (not (implies (not false) (not (and  (and ( < input.member 100) ( = assertion_disabled 0 ) )))) )  (not (implies (not false) (not (and  (and ( < input.sum 100) ( = assertion_disabled 0 ) )))) ) )
)
