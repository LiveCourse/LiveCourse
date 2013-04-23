<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');
require(APPPATH.'libraries/REST_Controller.php');

class S3 extends CI_Controller
{

	public function _remap()
	{
		$this->index();
	}

	public function index()
	{
		//include the S3 class
		$this->load->model('Model_S3');
		$this->Model_S3->setAuth('AKIAJMNWBMSJ72FOR3YA', '1rut3NDQNKHMUBA7/JPrLWCaIcpqA2pNc+oT2eMr');

		//check whether a form was submitted
		if(isset($_POST['Submit']))
		{

			//retreive post variables
			$fileName = $_FILES['theFile']['name'];
			$fileTempName = $_FILES['theFile']['tmp_name'];

			//move the file
			if ($this->Model_S3->putObjectFile($fileTempName, "livecourse", $fileName, 'public-read-write'))
			{
				echo "We successfully uploaded your file.";
			}
			else
			{
				echo "Something went wrong while uploading your file... sorry.";
			}
		}


		?>

		<form action="" method="post" enctype="multipart/form-data">
		  <input name="theFile" type="file" />
		  <input name="Submit" type="submit" value="Upload">
		</form>

		<?php
		// Get the contents of our bucket
		$bucket_contents = $this->Model_S3->getBucket("livecourse");
		$num = 1;

		foreach ($bucket_contents as $file)
		{
			$fname = $file['name'];
			$furl = "http://livecourse.s3.amazonaws.com/".$fname;

			//output a link to the file
			echo "$num)<a href=\"$furl\"> $fname</a><br />";
			$num++;
		}
	}
}
