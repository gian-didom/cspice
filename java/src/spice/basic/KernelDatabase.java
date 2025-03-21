
package spice.basic;

import spice.basic.CSPICE;
import spice.basic.SpiceKernelNotLoadedException;

/**
Class KernelDatabase supports loading and unloading of SPICE kernels.

<p> KernelDatabase currently offers only static methods for accessing
a single, default database. KernelDatabase serves as a placeholder
class for a future system that will support KernelDatabase instances.

<p> The current implementation of the KernelDatabase class relies
entirely on JNISpice methods.

<p> Version 1.0.0 27-OCT-2009 (NJB)

*/
public class KernelDatabase extends Object
{
   /*
   Static methods
   */

   /**
   Load one or more SPICE kernels into a program.
   <pre>

   -Procedure load ( Furnish a program with SPICE kernels )

   -Copyright

      Copyright (2003), California Institute of Technology.
      U.S. Government sponsorship acknowledged.

   -Required_Reading

       None.

   -Keywords

       UTILITY

   -Brief_I/O

      VARIABLE  I/O  DESCRIPTION
      --------  ---  --------------------------------------------------
      file       I   Name of SPICE kernel file (text or binary).

   -Detailed_Input

      file       is the name of a SPICE kernel file. The file may be
                 either binary or text. If the file is a binary SPICE
                 kernel it will be loaded into the appropriate SPICE
                 subsystem.  If `file' is a SPICE text kernel it will be
                 loaded into the kernel pool.  If `file' is a SPICE
                 meta-kernel containing initialization instructions
                 (through use of the correct kernel pool variables), the
                 files specified in those variables will be loaded into
                 the appropriate SPICE subsystem.

                 The SPICE text kernel format supports association of
                 names and data values using a "keyword = value" format.
                 The keyword-value pairs thus defined are called "kernel
                 variables."

                 While any information can be placed in a text kernel
                 file, the following string valued kernel variables are
                 recognized by SPICE as meta-kernel keywords:

                    KERNELS_TO_LOAD
                    PATH_SYMBOLS
                    PATH_VALUES

                 Each kernel variable is discussed below.

                    KERNELS_TO_LOAD   is a list of SPICE kernels to be
                                      loaded into a program.  If file
                                      names do not fit within the kernel
                                      pool 80 character limit, they may be
                                      continued to subsequent array
                                      elements by placing the continuation
                                      character ('+') at the end of an
                                      element and then placing the
                                      remainder of the file name in the
                                      next array element.  (See the
                                      examples below for an illustration
                                      of this technique or consult the
                                      routine CSPICE.stpool for further
                                      details.)

                                      Alternatively you may use a
                                      PATH_SYMBOL (see below) to
                                      substitute for some part of a file
                                      name.

                    PATH_SYMBOLS      is a list of strings (without
                                      embedded blanks), which if
                                      encountered following the '$'
                                      character will be replaced with the
                                      corresponding PATH_VALUES string.
                                      Note that PATH_SYMBOLS are
                                      interpreted only in the
                                      KERNELS_TO_LOAD variable. There must
                                      be a one-to-one correspondence
                                      between the values supplied for
                                      PATH_SYMBOLS and PATH_VALUES.

                    PATH_VALUES       is a list of expansions to use when
                                      PATH_SYMBOLS are encountered.  See
                                      the examples section for an
                                      illustration of use of PATH_SYMBOLS
                                      and PATH_VALUES.

                 These kernel pool variables persist within the kernel
                 pool only until all kernels associated with the
                 variable KERNELS_TO_LOAD have been loaded.  Once all
                 specified kernels have been loaded, the variables
                 KERNELS_TO_LOAD, PATH_SYMBOLS and PATH_VALUES are
                 removed from the kernel pool.

   -Detailed_Output

      None. The routine loads various SPICE kernels for use by your
      application.

   -Parameters

      None.

   -Files

      The input file is examined and loaded into the appropriate
      SPICE subsystem.  If the file is a meta-kernel, any kernels
      specified by the KERNELS_TO_LOAD keyword (and if present,
      the PATH_SYMBOLS and PATH_VALUES keywords) are loaded as well.

   -Exceptions

      All of the following problems will cause a SpiceErrorException
      to be thrown.


      1) If a problem is encountered while trying to load `file', it will
         be diagnosed by a routine from the appropriate SPICE subsystem.

      2) If the input `file' is a meta-kernel and some file in
         the KERNELS_TO_LOAD assignment cannot be found, the error
         SPICE(CANTFINDFILE) will be signaled and the routine will
         return.  Any files loaded prior to encountering the missing
         file will remain loaded.

      3) If an error is encountered while trying to load one of the files
         specified by the KERNELS_TO_LOAD assignment, the routine will
         discontinue attempting to perform any other tasks and return.

      4) If a PATH_SYMBOLS assignment is specified without a corresponding
         PATH_VALUES assignment, the error SPICE(NOPATHVALUE) will be
         signaled.

      5) If a meta-kernel is supplied that contains instructions
         specifying that another meta-text kernel be loaded, the error
         SPICE(RECURSIVELOADING) will be signaled.

      6) Any errors encountered during a call to an underlying
         C routine will result in a SPICE(JNIEXCEPTION) being signaled.

   -Particulars

      This routine provides a uniform interface to the SPICE kernel
      loading systems.  It allows you to easily assemble a list of
      SPICE kernels required by your application and to modify that set
      without modifying the source code of programs that make use of
      these kernels.

   -Examples

      Example 1
      ---------

      Load the leapseconds kernel naif0007.tls and the planetary ephemeris
      SPK file de405s.bsp.

         KernelDatabase.load ( "naif0007.tls" );
         KernelDatabase.load ( "de405s.bsp"   );


      Example 2
      ---------

      This example illustrates how you could create a meta-kernel file for
      a program that requires several text and binary kernels.

      First create a list of the kernels you need in a text file as
      shown below.

         \begintext

            Here are the SPICE kernels required for my application
            program.

            Note that kernels are loaded in the order listed. Thus we
            need to list the highest priority kernel last.


         \begindata

         KERNELS_TO_LOAD = ( '/home/mydir/kernels/spk/lowest_priority.bsp',
                             '/home/mydir/kernels/spk/next_priority.bsp',
                             '/home/mydir/kernels/spk/highest_priority.bsp',
                             '/home/mydir/kernels/text/leapsecond.ker',
                             '/home/mydir/kernels+',
                             '/custom+',
                             '/kernel_data/constants.ker',
                             '/home/mydir/kernels/text/sclk.tsc',
                             '/home/mydir/kernels/ck/c-kernel.bc' )


      Note that the file name

         /home/mydir/kernels/custom/kernel_data/constants.ker

      is continued across several lines in the right hand side of the
      assignment of the kernel variable KERNELS_TO_LOAD.

      Once you've created your list of kernels, call load near the
      beginning of your application program to load the meta-kernel
      automatically at program start up.

         KernelDatabase.load ( "myfile.txt" );

      This will cause each of the kernels listed in your meta-kernel
      to be loaded.


      Example 3
      ---------

      This example illustrates how you can simplify the previous
      kernel list by using PATH_SYMBOLS.


         \begintext

            Here are the SPICE kernels required for my application
            program.

            We are going to let A substitute for the directory that
            contains SPK files; B substitute for the directory that
            contains C-kernels; and C substitute for the directory that
            contains text kernels.  And we'll let D substitute for
            a "custom" directory that contains a special planetary
            constants kernel made just for our mission.

            Note that the order in which we list our PATH_VALUES must be
            the same order that the corresponding PATH_SYMBOLS are
            listed.


         \begindata

         PATH_VALUES  = ( '/home/mydir/kernels/spk',
                          '/home/mydir/kernels/ck',
                          '/home/mydir/kernels/text',
                          '/home/mydir/kernels/custom/kernel_data' )

         PATH_SYMBOLS = ( 'A',
                          'B',
                          'C'
                          'D'  )


         KERNELS_TO_LOAD = (  '$A/lowest_priority.bsp',
                              '$A/next_priority.bsp',
                              '$A/highest_priority.bsp',
                              '$C/leapsecond.ker',
                              '$D/constants.ker',
                              '$C/sclk.tsc',
                              '$B/c-kernel.bc'         )

   -Restrictions

      None.

   -Author_and_Institution

      C.H. Acton      (JPL)
      N.J. Bachman    (JPL)
      W.L. Taber      (JPL)

   -Literature_References

      None.

   -Version

     -JNISpice Beta Version 1.0.0, 18-NOV-2003 (NJB) (WLT) (CHA)

   </pre>


   @param file SPICE kernel or metakernel.

   @throws SpiceErrorException See the Exceptions section above.

   */
   public static void load ( String file )

      throws SpiceErrorException
   {
      CSPICE.furnsh ( file );
   }





   /**
   Unload a kernel from the SPICE system.

   <pre>
   -Procedure unload ( Unload a kernel )

   -Copyright

      Copyright (2003), California Institute of Technology.
      U.S. Government sponsorship acknowledged.

   -Required_Reading

      None.

   -Keywords

      KERNEL

   -Brief_I/O

      VARIABLE  I/O  DESCRIPTION
      --------  ---  --------------------------------------------------
      file       I   The name of a kernel to unload.

   -Detailed_Input

      file       is the name of a file to unload.  This file
                 should be one loaded through the interface

                    {@link spice.basic.KernelDatabase#load(String)}

                 If the file is not on the list of loaded kernels
                 no action is taken.

                 Note that if file is a meta-text kernel, all of
                 the files loaded as a result of loading the meta-text
                 kernel will be unloaded.

   -Detailed_Output

      None.

   -Parameters

      None.

   -Files

      None.

   -Exceptions

      1) If the specified kernel is not on the list of loaded kernels
         no action is taken.

      2) Any errors encountered during a JNISpice call to an underlying
         C routine will result in a SPICE(JNIEXCEPTION) being signaled.

   -Particulars

      The call

         KernelDatabase.unload ( file );

      has the effect of "erasing" the last previous call:

         KernelDatabase.load ( file );

      This interface allows you to unload binary and text kernels.
      Moreover, if you used a meta-text kernel to set up your
      working environment, you can unload all of the kernels loaded
      through the meta-kernel by unloading the meta-kernel.

      Unloading Text or Meta-text Kernels.

      Part of the action of unloading text (or meta-text kernels) is
      clearing the kernel pool and re-loading any kernels that were not in
      the specified set of kernels to unload.  Since loading of text
      kernels is not a very fast process, unloading text kernels takes
      considerably longer than unloading binary kernels.  Moreover, since
      the kernel pool is cleared, any kernel pool variables you have set
      from your program by using one of the methods

         CSPICE.pcpool
         CSPICE.pdpool
         CSPICE.pipool
         CSPICE.lmpool

      will be removed from the kernel pool.  For this reason, if you
      plan to use this feature in your program, together with one of
      the routines specified above, you will need to take special
      precautions to make sure kernel pool variables required by your
      program do not inadvertently disappear.

   -Examples

      Suppose that you wish to compare two different sets of kernels
      used to describe the geometry of a mission (for example a predict
      model and a reconstructed model). You can place all of the
      kernels for one model in one meta-text kernel, and the other set
      in a second meta-text kernel.  Let's call these predict.mta and
      actual.mta.


         KernelDatabase.load ( "predct.mta" );

         /.
         Compute quantities of interest and store them
         for comparison with results of reconstructed
         (actual) kernels.

         Now unload the predict model and load the reconstructed
         model.
         ./
         KernelDatabase.unload ( "predct.mta" );
         KernelDatabase.load   ( "actual.mta" );

         /.
         Re-compute quantities of interest and compare them
         with the stored quantities.
         ./


   -Restrictions

      See the note regarding the unloading of text and meta-text
      kernels.

   -Author_and_Institution

      N.J. Bachman    (JPL)
      W.L. Taber      (JPL)

   -Literature_References

      None.

   -Version

     -JNISpice Beta Version 1.0.0, 18-NOV-2003 (NJB) (WLT)

   -Index_Entries

      Unload a SPICE kernel

   </pre>
   @param file SPICE kernel or metakernel.

   @throws SpiceErrorException See the Exceptions section above.
   */
   public static void unload ( String file )

      throws SpiceErrorException
   {
      CSPICE.unload ( file );
   }




   /**
   Return the number of loaded kernels of a given type in the database.
   */
   public static synchronized int ktotal ( String kind )

      throws SpiceErrorException
   {
      return (  CSPICE.ktotal( kind )  );
   }


   /**
   Return the name of the file having a specified index in the
   set of files of a given kind.
   */
   public static synchronized String getFileName ( int    which,
                                                   String kind  )

      throws SpiceErrorException, SpiceKernelNotLoadedException
   {
      String[]    file   = new String[1];
      String[]    filtyp = new String[1];
      String[]    source = new String[1];
      int[]       handle = new int[1];
      boolean[]   found  = new boolean[1];

      CSPICE.kdata( which, kind, file, filtyp, source, handle, found );

      if ( !found[0] )
      {
         throw ( new SpiceKernelNotLoadedException(file[0])  );
      }

      return ( file[0] );
   }


   /**
   Return the source of the file having a specified file name.
   */
   public static synchronized String getSource ( String fileName )

      throws SpiceErrorException, SpiceKernelNotLoadedException
   {
      String[]    filtyp = new String[1];
      String[]    source = new String[1];
      int[]       handle = new int[1];
      boolean[]   found  = new boolean[1];

      CSPICE.kinfo ( fileName, filtyp, source, handle, found );

      if ( !found[0] )
      {
         throw ( new SpiceKernelNotLoadedException(fileName)  );
      }

      return ( source[0] );
   }



   /**
   Return the type of the file having a specified file name.
   */
   public static synchronized String getFileType ( String fileName )

      throws SpiceErrorException, SpiceKernelNotLoadedException
   {
      String[]    filtyp = new String[1];
      String[]    source = new String[1];
      int[]       handle = new int[1];
      boolean[]   found  = new boolean[1];

      CSPICE.kinfo ( fileName, filtyp, source, handle, found );

      if ( !found[0] )
      {
         throw ( new SpiceKernelNotLoadedException(fileName)  );
      }

      return ( filtyp[0] );
   }


   /**
   Return the source of the file having a specified file name.
   */
   public static synchronized int getHandle ( String fileName )

      throws SpiceErrorException, SpiceKernelNotLoadedException
   {
      String[]    filtyp = new String[1];
      String[]    source = new String[1];
      int[]       handle = new int[1];
      boolean[]   found  = new boolean[1];

      CSPICE.kinfo ( fileName, filtyp, source, handle, found );

      if ( !found[0] )
      {
         throw ( new SpiceKernelNotLoadedException(fileName)  );
      }


      if (    ( 0 == filtyp[0].trim().compareToIgnoreCase("TEXT") )
           || ( 0 == filtyp[0].trim().compareToIgnoreCase("META") )  )
      {
         //
         // This file has no associated handle.
         //

         throw ( new

            SpiceErrorException( "Kernel " + fileName + " has " +
                                 "type " + filtyp[0] + "; kernels " +
                                 "of this type don't have handles."  )  );

      }

      return ( handle[0] );
   }



   /**
   Clear and re-initialize the kernel database.
   */
   public static void clear()
   {
      CSPICE.kclear();
   }
}
