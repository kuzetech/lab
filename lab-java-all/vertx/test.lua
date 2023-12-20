function request()
   local str1 = 'This is a string.11111111111111111111111111'
   return wrk.format("POST", "/test", {}, str1)
end